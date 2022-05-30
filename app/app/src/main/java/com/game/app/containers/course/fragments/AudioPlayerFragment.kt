package com.game.app.containers.course.fragments

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.game.app.R
import com.game.app.constants.data.value.TargetSizeImageConstants
import com.game.app.constants.network.main.MainNetworkConstants
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.course.adapters.CourseSoundAdapter
import com.game.app.containers.course.models.CourseViewModel
import com.game.app.data.CourseFilesPreferences
import com.game.app.data.CoursePreferences
import com.game.app.data.CurrentListenAudioPreferences
import com.game.app.databinding.FragmentAudioPlayerBinding
import com.game.app.models.content.AudioDataModel
import com.game.app.models.content.AudioListDataModel
import com.game.app.models.content.ContentDataModel
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.blur.setupBlurView
import com.game.app.utils.enable
import com.game.app.utils.navigation
import com.game.app.utils.setMarginTop
import com.game.app.utils.visible
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.io.File
import java.lang.Exception
import java.lang.Runnable

class AudioPlayerFragment : BaseFragment<CourseViewModel, FragmentAudioPlayerBinding, UserRepository>() {

    private val _position: MutableLiveData<Int> = MutableLiveData()
    val position: LiveData<Int>
        get() = _position

    private fun setPosition(value: Int){
        _position.value = value
    }

    private var currentPosition: Int = 0
    private var dataAudios: AudioListDataModel? = null
    private lateinit var audioDataModel: AudioDataModel

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable: Runnable
    private var handler = Handler()

    private lateinit var courseSoundAdapter: CourseSoundAdapter
    private lateinit var coursePreferences: CoursePreferences
    private lateinit var courseFilesPreferences: CourseFilesPreferences
    private lateinit var currentListenAudioPreferences: CurrentListenAudioPreferences

    // Обработка одного из методов жизненного цикла фрагмента,
    // который обозначает что содержащая его активность вызвала
    // метод onCreate() и полностью создалась, таким образом
    // предоставив возможность использовать данные активности
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        coursePreferences = CoursePreferences(requireContext())
        courseFilesPreferences = CourseFilesPreferences(requireContext())
        currentListenAudioPreferences = CurrentListenAudioPreferences(requireContext())

        dataAudios = Gson().fromJson(
            arguments?.getString("audio_list"),
            AudioListDataModel::class.java
        )
        currentPosition = arguments?.getInt("current_position")!!
        audioDataModel = dataAudios!!.audioList?.get(currentPosition)!!

        setPosition(currentPosition)
        position.observe(viewLifecycleOwner){
            if((it - 1) < 0){
                binding.imageButtonPrev.enable(false)
            }else{
                binding.imageButtonPrev.enable(true)
            }

            if((it + 1) >= dataAudios!!.audioList!!.size){
                binding.imageButtonNext.enable(false)
            }else{
                binding.imageButtonNext.enable(true)
            }
        }

        binding.toolbar.setMarginTop(128)

        binding.toolbar.setNavigationOnClickListener {
            navigation(R.id.action_audioPlayerFragment_to_courseFragment)
        }

        val imageView = ImageView(context)
        Picasso.get()
            .load((MainNetworkConstants.SERVER_MAIN_ADDRESS + "//" + audioDataModel.titleImgPath)
                .replace("\\", "/"))
            .resize(TargetSizeImageConstants.WIDTH_STD, TargetSizeImageConstants.HEIGHT_STD)
            .centerCrop()
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    binding.imageViewBackground.setImageDrawable(imageView.drawable)
                }

                override fun onError(e: Exception?) {}
            })

        binding.textViewTitle.text = audioDataModel.title
        binding.textViewLessonNum.text = audioDataModel.type
        binding.textViewDescription.text = audioDataModel.description

        setupBlurView(binding.blurView,
            binding.constraintLayoutAudio,
            binding.imageViewBackground.drawable,
            requireContext(),
            5f
        )

        setupBlurView(binding.blurViewPlay,
            binding.constraintLayoutAudio,
            binding.imageViewBackground.drawable,
            requireContext(),
            10f
        )

        setupBlurView(binding.blurViewPrev5,
            binding.constraintLayoutAudio,
            binding.imageViewBackground.drawable,
            requireContext(),
            10f
        )

        setupBlurView(binding.blurViewNext5,
            binding.constraintLayoutAudio,
            binding.imageViewBackground.drawable,
            requireContext(),
            10f
        )

        setupBlurView(binding.blurViewPrev,
            binding.constraintLayoutAudio,
            binding.imageViewBackground.drawable,
            requireContext(),
            10f
        )

        setupBlurView(binding.blurViewNext,
            binding.constraintLayoutAudio,
            binding.imageViewBackground.drawable,
            requireContext(),
            10f
        )

        mediaPlayer = MediaPlayer.create(requireContext(), Uri.fromFile(File(audioDataModel.soundPath!!)))
        binding.slider.progress = audioDataModel.currentDuration
        mediaPlayer.seekTo(audioDataModel.currentDuration)
        binding.slider.max = mediaPlayer.duration

        binding.imageButtonPlay.setOnClickListener {
            if(!mediaPlayer.isPlaying){
                mediaPlayer.start()
                binding.imageButtonPlay.setImageResource(R.drawable.ic_pause)
            }else{
                mediaPlayer.pause()
                binding.imageButtonPlay.setImageResource(R.drawable.ic_play)
            }
        }

        binding.slider.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2){
                    mediaPlayer.seekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        runnable = kotlinx.coroutines.Runnable {
            binding.slider.progress = mediaPlayer.currentPosition
            handler.postDelayed(runnable, 1000)
        }

        handler.postDelayed(runnable, 1000)

        mediaPlayer.setOnCompletionListener {
            binding.imageButtonPlay.setImageResource(R.drawable.ic_play)
            mediaPlayer.seekTo(0)
            binding.slider.progress = 0

            if(checkAuth(null, false)){
                val auth = getAuthData()

                val data = Gson().fromJson(
                    runBlocking {
                        coursePreferences.course.first()
                    }.toString(),
                    ContentDataModel::class.java
                )

                runBlocking {
                    viewModel.setCompleteSound(
                        usersId = auth!!.usersId,
                        coursesId = data.id!!,
                        soundsId = audioDataModel.id
                    )
                }

                runBlocking {
                    coursePreferences.saveSounds("")
                }

                viewModel.getAllSounds(data.id!!)
            }
        }

        binding.imageButtonNext5.setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition + 5000)
            binding.slider.progress = mediaPlayer.currentPosition
        }

        binding.imageButtonPrev5.setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition - 5000)
            binding.slider.progress = mediaPlayer.currentPosition
        }

        if(audioDataModel.currentDuration > 0){
            mediaPlayer.start()
            binding.imageButtonPlay.setImageResource(R.drawable.ic_pause)
        }


        // Handler for button prev and next
        binding.imageButtonNext.setOnClickListener {
            binding.progressBarAudio.visible(true)

            if((currentPosition + 1) < dataAudios!!.audioList!!.size){
                currentPosition++
                setPosition(currentPosition)
                audioDataModel = dataAudios!!.audioList?.get(currentPosition)!!

                mediaPlayer.stop()
                mediaPlayer.release()

                mediaPlayer = MediaPlayer.create(requireContext(), Uri.fromFile(File(audioDataModel.soundPath!!)))
                binding.slider.progress = audioDataModel.currentDuration
                mediaPlayer.seekTo(audioDataModel.currentDuration)
                binding.slider.max = mediaPlayer.duration
                binding.imageButtonPlay.setImageResource(R.drawable.ic_play)

                val imageView = ImageView(context)
                Picasso.get()
                    .load((MainNetworkConstants.SERVER_MAIN_ADDRESS + "//" + audioDataModel.titleImgPath)
                        .replace("\\", "/"))
                    .resize(TargetSizeImageConstants.WIDTH_STD, TargetSizeImageConstants.HEIGHT_STD)
                    .centerCrop()
                    .into(imageView, object : Callback {
                        override fun onSuccess() {
                            binding.imageViewBackground.setImageDrawable(imageView.drawable)
                        }

                        override fun onError(e: Exception?) {}
                    })

                binding.textViewTitle.text = audioDataModel.title
                binding.textViewLessonNum.text = audioDataModel.type
                binding.textViewDescription.text = audioDataModel.description
            }

            binding.progressBarAudio.visible(false)
        }

        binding.imageButtonPrev.setOnClickListener {
            binding.progressBarAudio.visible(true)

            if((currentPosition - 1) >= 0){
                currentPosition--
                setPosition(currentPosition)
                audioDataModel = dataAudios!!.audioList?.get(currentPosition)!!

                mediaPlayer.stop()
                mediaPlayer.release()

                mediaPlayer = MediaPlayer.create(requireContext(), Uri.fromFile(File(audioDataModel.soundPath!!)))
                binding.slider.progress = audioDataModel.currentDuration
                mediaPlayer.seekTo(audioDataModel.currentDuration)
                binding.slider.max = mediaPlayer.duration
                binding.imageButtonPlay.setImageResource(R.drawable.ic_play)

                val imageView = ImageView(context)
                Picasso.get()
                    .load((MainNetworkConstants.SERVER_MAIN_ADDRESS + "//" + audioDataModel.titleImgPath)
                        .replace("\\", "/"))
                    .resize(TargetSizeImageConstants.WIDTH_STD, TargetSizeImageConstants.HEIGHT_STD)
                    .centerCrop()
                    .into(imageView, object : Callback {
                        override fun onSuccess() {
                            binding.imageViewBackground.setImageDrawable(imageView.drawable)
                        }

                        override fun onError(e: Exception?) {}
                    })

                binding.textViewTitle.text = audioDataModel.title
                binding.textViewLessonNum.text = audioDataModel.type
                binding.textViewDescription.text = audioDataModel.description
            }

            binding.progressBarAudio.visible(false)
        }
    }

    override fun getViewModel() = CourseViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAudioPlayerBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() : UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences))
    }

    override fun onDestroyView() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
        handler.removeCallbacks(runnable)

        super.onDestroyView()
    }
}