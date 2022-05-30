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

class CalendarViewModel(
    private val repository: UserRepository
): BaseViewModel(repository) {
    private val _currentPosition: MutableLiveData<Int> = MutableLiveData()
    val currentPosition: LiveData<Int>
        get() = _currentPosition

    private val _currentCourses: MutableLiveData<Resource<Response<ResponseBody>>> = MutableLiveData()
    val currentCourses: LiveData<Resource<Response<ResponseBody>>>
        get() = _currentCourses

    fun setPosition(
        position: Int
    ) = viewModelScope.launch {
        _currentPosition.value = position
    }

    fun getCourses(date: String) = viewModelScope.launch {
        _currentCourses.value = Resource.Loading
        _currentCourses.value = repository.getCoursesByDate(date)
    }
}