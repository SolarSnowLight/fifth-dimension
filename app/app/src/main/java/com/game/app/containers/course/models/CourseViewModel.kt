package com.game.app.containers.course.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.game.app.containers.base.BaseViewModel
import com.game.app.network.Resource
import com.game.app.repositories.UserRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class CourseViewModel(
    private val repository: UserRepository
): BaseViewModel(repository) {
    private val _sounds: MutableLiveData<Resource<Response<ResponseBody>>> = MutableLiveData()
    val sounds: LiveData<Resource<Response<ResponseBody>>>
        get() = _sounds

    private val _soundsFiles: MutableLiveData<Resource<Response<ResponseBody>>> = MutableLiveData()
    val soundsFiles: LiveData<Resource<Response<ResponseBody>>>
        get() = _soundsFiles

    private val _soundsLabels: MutableLiveData<Resource<Response<ResponseBody>>> = MutableLiveData()
    val soundsLabels: LiveData<Resource<Response<ResponseBody>>>
        get() = _soundsLabels

    private val _soundsImages: MutableLiveData<Resource<Response<ResponseBody>>> = MutableLiveData()
    val soundsImages: LiveData<Resource<Response<ResponseBody>>>
        get() = _soundsImages

    private val _subscribe: MutableLiveData<Resource<Response<ResponseBody>>> = MutableLiveData()
    val subscribe: LiveData<Resource<Response<ResponseBody>>>
        get() = _subscribe

    // Получение списка информации обо всех звуковых файлах данного курса
    fun getAllSounds(coursesId: Int) = viewModelScope.launch {
        _sounds.value = Resource.Loading
        _sounds.value = repository.getAllSounds(coursesId)
    }

    // Получение изображения определённого урока
    fun getTitleImgSound(soundsId: Int) = viewModelScope.launch {
        _soundsImages.value = Resource.Loading
        _soundsImages.value = repository.getTitleImgSound(soundsId)
    }

    // Получение звукового файла определённого урока
    fun getSoundFile(usersId: String, coursesId: Int, soundsId: Int) = viewModelScope.launch {
        _soundsFiles.value = Resource.Loading
        _soundsFiles.value = repository.getSoundFile(usersId, coursesId, soundsId)
    }

    // Set complete sound
    fun setCompleteSound(usersId: String, coursesId: Int, soundsId: Int) = viewModelScope.launch {
        _soundsLabels.value = Resource.Loading
        _soundsLabels.value = repository.setCompleteSound(usersId, coursesId, soundsId)
    }

    // Get subscribe flag
    suspend fun getSubscribeUser(usersId: String) = viewModelScope.launch {
        _subscribe.value = Resource.Loading
        _subscribe.value = repository.checkSubscribe(usersId)
    }
}