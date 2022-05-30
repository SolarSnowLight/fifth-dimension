package com.game.app.containers.course.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import com.game.app.R
import com.game.app.constants.data.value.TargetSizeImageConstants
import com.game.app.constants.network.main.MainNetworkConstants
import com.game.app.containers.base.BaseAdapter
import com.game.app.data.CurrentListenAudioPreferences
import com.game.app.databinding.AdapterCourseSoundItemBinding
import com.game.app.models.content.AudioDataModel
import com.game.app.models.content.AudioListDataModel
import com.game.app.utils.audio.getDurationString
import com.game.app.utils.visible
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.Exception

class CourseSoundAdapter(
    private val context: Context,
    private var audios: ArrayList<AudioDataModel>,
    private val currentListenAudioPreferences: CurrentListenAudioPreferences,
    private val onCompletionListener: (Int) -> Unit
) : BaseAdapter<AudioDataModel, AdapterCourseSoundItemBinding>() {
    private var mediaPlayer: MediaPlayer? = null
    private var runnable: Runnable? = null
    private var handler = Handler()
    private var positionListen: Int? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setSounds(audiosInput: ArrayList<AudioDataModel>){
        audios.clear()
        audios = audiosInput
        notifyDataSetChanged()
    }

    fun setSoundPosition(position: Int, filePath: String){
        audios[position].soundPath = filePath
        notifyItemChanged(position)
    }

    fun setListeningFlag(position: Int, listening: Boolean){
        audios[position].isListening = listening
        notifyItemChanged(position)
    }

    fun setLoading(soundsId: Int, loading: Boolean = false){
        val index = audios.indexOfFirst {
            it.id == soundsId
        }

        if(index >= 0){
            audios[index].loading = loading
            notifyItemChanged(index)
        }
    }

    override fun getAdapterBinding(parent: ViewGroup, viewType: Int): BaseViewHolder<AdapterCourseSoundItemBinding> {
        val binding = AdapterCourseSoundItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterCourseSoundItemBinding>, @SuppressLint(
        "RecyclerView"
    ) position: Int) {
        val audio = audios[position]

        holder.binding.textViewLesson.text = audio.type
        holder.binding.textViewTitle.text = "${audio.lessonNum}. ${audio.title}"

        val imageView = ImageView(context)
        Picasso.get()
            .load((MainNetworkConstants.SERVER_MAIN_ADDRESS + "//" + audio.titleImgPath)
                .replace("\\", "/"))
            .resize(TargetSizeImageConstants.WIDTH_MIN, TargetSizeImageConstants.HEIGHT_MIN)
            .centerCrop()
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    holder.binding.circleImageView.setImageDrawable(imageView.drawable)
                }

                override fun onError(e: Exception?) {}
            })

        val conditionNotNull = (audio.titleImgPath != null) && (audio.soundPath != null)

        if(audio.duration != null){
            holder.binding.textViewDuration.text = audio.duration
        }else{
            holder.binding.textViewDuration.text = ""
        }

        /*if(audio.titleImgPath != null){
            holder.binding.circleImageView.setImageDrawable(Drawable.createFromPath(audio.titleImgPath))
        }*/

        if(conditionNotNull){
            val media = MediaPlayer.create(context, Uri.fromFile(File(audio.soundPath!!)))

            if(media != null){
                holder.binding.progressBarSound.visible(false)
                holder.binding.buttonSound.visible(true)
                holder.binding.circularProgressBar.visibility = View.VISIBLE
                val duration = media.duration
                audio.maxDuration = duration

                holder.binding.textViewDuration.text = getDurationString((audio.maxDuration!! - audio.currentDuration))
                holder.binding.circularProgressBar.progressMax = duration.toFloat()
                holder.binding.circularProgressBar.progress = audio.currentDuration.toFloat()
                holder.binding.buttonSound.setImageResource(
                    if(audio.isListening) R.drawable.ic_pause else R.drawable.ic_play
                )

                holder.binding.circleImageView.setOnClickListener {
                    if(positionListen == null){
                        // Создание нового плеера
                        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(File(audio.soundPath!!)))
                        audio.currentDuration = 0
                    }else if(positionListen != position){
                        // Обновление прослушивания музыкального произведения
                        clearMediaPlayer()

                        // Обновление плеера
                        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(File(audio.soundPath!!)))

                        // Перемещение на прошлое место
                        mediaPlayer?.seekTo(audio.currentDuration)

                        runBlocking {
                            setListeningFlag(positionListen!!, false)
                        }
                    }

                    positionListen = position

                    if(audio.isListening){
                        // Если элемент уже прослушиватеся, то поставить его на паузу
                        audio.isListening = false
                        mediaPlayer?.pause()
                        holder.binding.buttonSound.setImageResource(R.drawable.ic_play)
                    }else{
                        // Иначе сново начать его слушать
                        audio.isListening = true
                        mediaPlayer?.start()
                        holder.binding.buttonSound.setImageResource(R.drawable.ic_pause)
                    }

                    mediaPlayer?.setOnCompletionListener {
                        clearMediaPlayer()
                        runBlocking {
                            setListeningFlag(positionListen!!, false)
                        }
                        audio.currentDuration = 0
                        positionListen = null

                        onCompletionListener(audio.id)
                    }

                    // Начало потока для обновления прогрессбара
                    runnable = kotlinx.coroutines.Runnable {
                        holder.binding.circularProgressBar.progress = mediaPlayer?.currentPosition!!.toFloat()

                        //holder.binding.circularProgressBar.setProgressWithAnimation(mediaPlayer.currentPosition.toFloat(), 0)
                        audio.currentDuration = mediaPlayer!!.currentPosition
                        holder.binding.textViewDuration.text = getDurationString((audio.maxDuration!! - audio.currentDuration))

                        handler.postDelayed(runnable!!, 1000)
                    }

                    handler.postDelayed(runnable!!, 1000)
                }
            }

            holder.binding.linearLayoutAdapter.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("current_position", position)
                bundle.putString("audio_list", Gson().toJson(
                    AudioListDataModel(
                        audioList = audios.filter { it ->
                            it.soundPath != null
                        } as ArrayList<AudioDataModel>
                    )
                ))

                it.findNavController().navigate(
                    R.id.action_courseFragment_to_audioPlayerFragment,
                    bundle
                )
            }
        }else{
            if(!audio.loading){
                holder.binding.progressBarSound.visible(false)
            }

            holder.binding.buttonSound.visible(true)
            holder.binding.buttonSound.setImageResource(
                R.drawable.ic_lock
            )
        }
    }

    override fun getItemCount(): Int {
        return audios.size
    }

    fun clearMediaPlayer() {
        if(mediaPlayer != null){
            /*mediaPlayer?.stop()
            mediaPlayer?.reset()*/
            mediaPlayer?.release()
        }

        if(runnable != null){
            handler.removeCallbacks(runnable!!)
        }
    }
}
