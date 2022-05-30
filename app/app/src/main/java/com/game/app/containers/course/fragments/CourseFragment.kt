package com.game.app.containers.course.fragments

import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.game.app.constants.data.value.TargetSizeImageConstants
import com.game.app.constants.network.main.MainNetworkConstants
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.course.adapters.CourseSoundAdapter
import com.game.app.containers.course.models.CourseViewModel
import com.game.app.data.CourseFilesPreferences
import com.game.app.data.CoursePreferences
import com.game.app.data.CurrentListenAudioPreferences
import com.game.app.databinding.FragmentCourseBinding
import com.game.app.models.content.AudioDataModel
import com.game.app.models.content.ContentDataModel
import com.game.app.models.error.ErrorModel
import com.game.app.models.sound.*
import com.game.app.models.subscribe.SubscribeModel
import com.game.app.network.Resource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.*
import com.game.app.utils.recycler.setAdapter
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Exception


class CourseFragment :  BaseFragment<CourseViewModel, FragmentCourseBinding, UserRepository>() {
    private lateinit var courseSoundAdapter: CourseSoundAdapter
    private lateinit var coursePreferences: CoursePreferences
    private lateinit var courseFilesPreferences: CourseFilesPreferences
    private lateinit var currentListenAudioPreferences: CurrentListenAudioPreferences

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Инициализация внутренних хранилищ
        coursePreferences = CoursePreferences(requireContext())
        courseFilesPreferences = CourseFilesPreferences(requireContext())
        currentListenAudioPreferences = CurrentListenAudioPreferences(requireContext())

        val authVerificationResult = checkAuth(null, false)

        val onCompletionListener: (soundsId: Int) -> Unit = {
            if(authVerificationResult){
                val auth = getAuthData()

                // Update sounds
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
                        soundsId = it
                    )
                }

                runBlocking {
                    coursePreferences.saveSounds("")
                }

                viewModel.getAllSounds(data.id!!)
            }
        }

        if(!authVerificationResult){
            binding.authButton.visible(true)

            binding.authButton.setOnClickListener {
                toAuth()
            }
        }else{
            val data = Gson().fromJson(
                runBlocking {
                    coursePreferences.course.first()
                }.toString(),
                ContentDataModel::class.java
            )

            if(data.subscription){
                runBlocking {
                    viewModel.getSubscribeUser(getAuthData()?.usersId!!)
                }
            }
        }

        // Инициализация адаптера
        courseSoundAdapter = CourseSoundAdapter(
            requireContext(),
            audios = arrayListOf(),
            currentListenAudioPreferences,
            onCompletionListener = onCompletionListener
        )

        // Добавление Adapter к RecyclerView
        binding.recyclerViewAudioCourse.setAdapter(
            requireContext(),
            courseSoundAdapter,
            RecyclerView.VERTICAL
        )

        // Добавление отступа в toolbar
        binding.toolbar.setMarginTop(128)
        binding.blurView.setupWith(binding.appBarLayout)
            .setFrameClearDrawable(binding.imageViewToolbar.drawable)
            .setBlurAlgorithm(RenderScriptBlur(context))
            .setBlurRadius(0.01f)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(false)

        // Обработка изменений данных о текущем курсе
        coursePreferences.course.asLiveData().observe(requireActivity()){
            if((it != null) && (it.isNotEmpty())){
                val data = Gson().fromJson(
                    it,
                    ContentDataModel::class.java
                )

                val imageView = ImageView(context)
                Picasso.get()
                    .load((MainNetworkConstants.SERVER_MAIN_ADDRESS + "//" + data.titleImgPath)
                        .replace("\\", "/"))
                    .resize(TargetSizeImageConstants.WIDTH_STD, TargetSizeImageConstants.HEIGHT_STD)
                    .centerCrop()
                    .into(imageView, object : Callback {
                        override fun onSuccess() {
                            binding.imageViewToolbar.setImageDrawable(imageView.drawable)
                        }

                        override fun onError(e: Exception?) {}
                    })

                binding.textViewTitleCourse.text = data.title
                binding.editTextDescriptionCourse.setText(data.description)

                viewModel.getAllSounds(data.id!!)

                // Получение уроков по данному курсу
                /*val sounds = runBlocking {
                    coursePreferences.sounds.first()
                }

                if((sounds != null) && (sounds.isEmpty())){
                    val dataSounds = Gson().fromJson(
                        sounds,
                        SoundDataModel::class.java
                    )

                    if(dataSounds.sounds.size != data.sounds!!.size){
                        viewModel.getAllSounds(data.id!!)
                    }else{
                        for(i in 0..data.sounds!!.size){
                            if((dataSounds.sounds[i].id != data.sounds!![i].id)){
                                viewModel.getAllSounds(data.id!!)
                                break
                            }
                        }
                    }
                }else{
                    viewModel.getAllSounds(data.id!!)
                }*/
            }
        }

        // Обработка загрузки уроков
        coursePreferences.sounds.asLiveData().observe(requireActivity()){
            binding.progressBarCourse.visible(true)

            if((it != null) && (it.isNotEmpty())){
                val soundsData = Gson().fromJson(
                    it,
                    SoundDataModel::class.java
                )

                // Получение ссылок на файлы изображений каждого урока
               /* val soundsImages = runBlocking {
                    courseFilesPreferences.soundsImages.first()
                }*/

                // Получение ссылок на звуковые файлы каждого урока
                val soundsFiles = runBlocking {
                    courseFilesPreferences.soundsFile.first()
                }

                /*val soundsImagesData = if(soundsImages != null)
                    Gson().fromJson(
                        soundsImages,
                        SoundImgDataModel::class.java
                    )
                else
                    null*/

                val soundsFilesData = if(soundsFiles != null)
                    Gson().fromJson(
                        soundsFiles,
                        SoundFileDataModel::class.java
                    )
                else
                    null

                val courseDataPreferences = runBlocking {
                    coursePreferences.course.first()
                }

                val courseData = Gson().fromJson(
                    courseDataPreferences,
                    ContentDataModel::class.java
                )

                if((soundsData != null) && (soundsData.sounds.isNotEmpty())){
                    courseSoundAdapter.setSounds(
                        soundsData.sounds.map{ it ->
                            // Получение изображения урока по sounds_id
                            /*val image = getImageBySound(it.id!!, soundsImagesData)

                            if(image == null){
                                viewModel.getTitleImgSound(it.id!!)
                            }*/

                            // Получение звукового файла урока по sounds_id
                            var soundFile = getSoundFileBySound(it.id!!, soundsFilesData)
                            var loading = true

                            if(soundFile == null){
                                if(authVerificationResult){
                                    val auth = getAuthData()
                                    viewModel.getSoundFile(
                                        usersId = auth!!.usersId,
                                        soundsId = it.id!!,
                                        coursesId = courseData.id!!
                                    )
                                }else{
                                    loading = false
                                }
                            }else if(!authVerificationResult){
                                loading = false
                                soundFile = null
                            }

                            return@map AudioDataModel(
                                id = it.id!!,
                                title = it.title!!,
                                lessonNum = it.lessonNum!!,
                                titleImgPath = it.titleImgPath,
                                soundPath = soundFile?.filePath,
                                loading = loading,
                                description = it.description,
                                type = it.type
                            )
                        } as ArrayList<AudioDataModel>
                    )
                }
            }

            binding.progressBarCourse.visible(false)
        }

        viewModel.subscribe.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    if(it.value.isSuccessful){
                        lifecycleScope.launch {
                            val data = Gson().fromJson(
                                Gson().toJson(
                                    JsonParser.parseString(
                                        it.value.body()?.string()
                                    )
                                ),
                                SubscribeModel::class.java
                            )

                            withContext(Dispatchers.Main){
                                if(!data.subscribe){
                                    binding.buyButton.visible(true)
                                }
                            }
                        }
                    }else{
                        handleErrorMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it)
                }
                else -> {}
            }
        }

        // Обработка загрузки списка звуковых файлов данного курса
        viewModel.sounds.observe(viewLifecycleOwner){
            // binding.progressBarCourse.visible(it is Resource.Loading)
            when(it){
                is Resource.Success -> {
                    if(it.value.isSuccessful){
                        lifecycleScope.launch {
                            coursePreferences.saveSounds(
                                Gson().toJson(
                                    JsonParser.parseString(
                                        it.value.body()?.string()
                                    )
                                )
                            )
                        }
                    }else{
                        handleMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it)
                }
                else -> {}
            }
        }

        // Определение поведения в случае загрузки изображения курса
        /*viewModel.soundsImages.observe(viewLifecycleOwner){
            // binding.progressBarCourse.visible(it is Resource.Loading)

            when(it){
                is Resource.Success -> {
                    if(it.value.isSuccessful){
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO){
                                val data = runBlocking {
                                    courseFilesPreferences.soundsImages.first()
                                }

                                val sounds = runBlocking {
                                    coursePreferences.sounds.first()
                                }

                                val soundsDataObj = Gson().fromJson(
                                    sounds,
                                    SoundDataModel::class.java
                                )

                                val soundsId = it.value.headers()["sounds_id"].toString().toInt()
                                var filePath: String? = null

                                if(data == null){
                                    // Запись файла на локальное хранилище
                                    filePath = writeResponseBodyToStorage(it.value.body()!!,
                                        it.value.headers()["filename"].toString()
                                    ) ?: return@withContext

                                    // В случае, если до этого момента не было загружено ни одного
                                    // изображения курса, то загрузить первое изображение
                                    runBlocking {
                                        courseFilesPreferences.saveSoundsImages(
                                            Gson().toJson(
                                                SoundImgDataModel(
                                                    titleImages = arrayListOf(
                                                        SoundImgItemModel(
                                                            soundsId = soundsId,
                                                            filename = it.value.headers()["filename"].toString(),
                                                            filePath = filePath!!,
                                                            subscribe = it.value.headers()["subscribe"].toString().toBoolean()
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    }
                                }else{
                                    val dataSoundsImages = Gson().fromJson(data, SoundImgDataModel::class.java)

                                    if(!existsElementByIdSoundImg(
                                            soundsId,
                                            dataSoundsImages
                                    )){
                                        filePath = writeResponseBodyToStorage(it.value.body()!!,
                                            it.value.headers()["filename"].toString()
                                        ) ?: return@withContext

                                        dataSoundsImages.titleImages.add(
                                            SoundImgItemModel(
                                                soundsId = soundsId,
                                                filename = it.value.headers()["filename"].toString(),
                                                filePath = filePath,
                                                subscribe = it.value.headers()["subscribe"].toString().toBoolean()
                                            )
                                        )

                                        runBlocking {
                                            courseFilesPreferences.saveSoundsImages(
                                                Gson().toJson(dataSoundsImages)
                                            )
                                        }
                                    }
                                }

                                val position = soundsDataObj.sounds.indexOfFirst { it ->
                                    it.id == soundsId
                                }

                                Log.i("MYTAG", position.toString())
                                Log.i("MYTAG", soundsId.toString())
                                Log.i("MYTAG", filePath!!)

                                withContext(Dispatchers.Main){
                                    if(position >= 0){
                                        courseSoundAdapter.setImagePosition(
                                            (soundsDataObj.sounds[position].lessonNum!! - 1),
                                            filePath!!
                                        )
                                    }
                                }
                            }
                        }
                    }else{
                        handleMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it)
                }
                else -> {}
            }
        }*/

        // Определение поведения в случае загрузки изображения курса
        viewModel.soundsFiles.observe(viewLifecycleOwner){
            // binding.progressBarCourse.visible(it is Resource.Loading)

            when(it){
                is Resource.Success -> {
                    if(it.value.isSuccessful){
                        if(it.value.code() != 201){
                            courseSoundAdapter.setLoading(it.value.headers()["sounds_id"].toString().toInt())

                            return@observe
                        }

                        lifecycleScope.launch {
                            withContext(Dispatchers.IO){
                                val data = runBlocking {
                                    courseFilesPreferences.soundsFile.first()
                                }

                                val sounds = runBlocking {
                                    coursePreferences.sounds.first()
                                }

                                val soundsDataObj = Gson().fromJson(
                                    sounds,
                                    SoundDataModel::class.java
                                )

                                val soundsId = it.value.headers()["sounds_id"].toString().toInt()
                                var filePath: String? = null

                                if(data == null){
                                    // Запись файла на локальное хранилище
                                    filePath = writeResponseBodyToStorage(it.value.body()!!,
                                        it.value.headers()["filename"].toString(),
                                        environment = Environment.DIRECTORY_MUSIC,
                                        type = ".mp3"
                                    ) ?: return@withContext

                                    // В случае, если до этого момента не было загружено ни одного
                                    // изображения курса, то загрузить первое изображение
                                    runBlocking {
                                        courseFilesPreferences.saveSoundFiles(
                                            Gson().toJson(
                                                SoundFileDataModel(
                                                    soundFiles = arrayListOf(
                                                        SoundFileItemModel(
                                                            soundsId = soundsId,
                                                            filename = it.value.headers()["filename"].toString(),
                                                            filePath = filePath!!
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    }
                                }else{
                                    val dataSoundFile = Gson().fromJson(data, SoundFileDataModel::class.java)
                                    if(!existsElementByIdSoundFile(
                                            soundsId,
                                            dataSoundFile
                                        )
                                    ){
                                        filePath = writeResponseBodyToStorage(it.value.body()!!,
                                            it.value.headers()["filename"].toString(),
                                            environment = Environment.DIRECTORY_MUSIC,
                                            type = ".mp3"
                                        ) ?: return@withContext

                                        dataSoundFile.soundFiles.add(
                                            SoundFileItemModel(
                                                soundsId = soundsId,
                                                filename = it.value.headers()["filename"].toString(),
                                                filePath = filePath!!
                                            )
                                        )

                                        runBlocking {
                                            courseFilesPreferences.saveSoundFiles(
                                                Gson().toJson(dataSoundFile)
                                            )
                                        }
                                    }
                                }


                                val position = soundsDataObj.sounds.indexOfFirst { it ->
                                    it.id == soundsId
                                }

                                withContext(Dispatchers.Main){
                                    if(position >= 0){
                                        courseSoundAdapter.setSoundPosition(
                                            (soundsDataObj.sounds[position].lessonNum!! - 1),
                                            filePath!!
                                        )
                                    }

                                    courseSoundAdapter.setLoading(soundsId)
                                }
                                //binding.recyclerViewAudioCourse.post(Runnable { myadapter.notifyDataSetChanged() })
                            }
                        }
                    }else{
                        handleMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it)
                }
                else -> {}
            }
        }

        /*binding.recyclerViewAudioCourse.addOnItemTouchListener(
            RecyclerItemClickListener(context,
                binding.recyclerViewAudioCourse,
                object: RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        // navigation(R.id.action_courseFragment_to_audioPlayerFragment)
                        // viewModel.setPosition(position)
                        // view?.setBackgroundResource(R.drawable.vector_calendar_item)
                    }

                    override fun onLongItemClick(view: View?, position: Int) {}

                    override fun onItemClick(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                    }
                })
        )*/

        /*currentListenAudioPreferences.currentSound.asLiveData().observe(viewLifecycleOwner){
            if((it != null) && (it.isNotEmpty())){
                val data = Gson().fromJson(
                    it,
                    SoundCurrentModel::class.java
                )

                mediaPlayer = MediaPlayer.create(
                    requireContext(),
                    Uri.fromFile(File(data.filePath))
                )
            }
        }*/

        binding.buyButton.setOnClickListener {
            toPayment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun getViewModel() = CourseViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCourseBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() : UserRepository {
        val data = runBlocking { userPreferences.authData.first() }
        return if((data != null) && (data.isNotEmpty()))
            UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences))
        else
            UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences, false))
    }

    override fun onDestroy() {
        runBlocking {
            coursePreferences.clear()
            currentListenAudioPreferences.clear()
        }

        super.onDestroy()
    }

    override fun onDestroyView() {
        courseSoundAdapter.clearMediaPlayer()

        super.onDestroyView()
    }
}