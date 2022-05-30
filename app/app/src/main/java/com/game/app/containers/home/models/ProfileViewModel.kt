package com.game.app.containers.home.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.game.app.containers.base.BaseViewModel
import com.game.app.network.Resource
import com.game.app.repositories.UserRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class ProfileViewModel(
    private val repository: UserRepository
): BaseViewModel(repository) {
    // Данные о пользователе
    private val _userInfo: MutableLiveData<Resource<Response<ResponseBody>>> = MutableLiveData()
    val userInfo: LiveData<Resource<Response<ResponseBody>>>
        get() = _userInfo

    fun getUserInfo(usersId: String) = viewModelScope.launch {
        _userInfo.value = Resource.Loading
        _userInfo.value = repository.getUserInfo(usersId)
    }

    fun updateUserInfo(usersId: String, name: String, surname: String, password: String? = null) = viewModelScope.launch{
        _userInfo.value = Resource.Loading
        _userInfo.value = repository.updateUserInfo(usersId, name, surname, password)
    }
}