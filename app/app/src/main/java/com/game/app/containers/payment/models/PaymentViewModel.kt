package com.game.app.containers.payment.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.game.app.containers.base.BaseViewModel
import com.game.app.network.Resource
import com.game.app.repositories.UserRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class PaymentViewModel(
    private val repository: UserRepository
): BaseViewModel(repository) {

    // Payment info: check payment, info payment, etc.
    private val _paymentInfo: MutableLiveData<Resource<Response<ResponseBody>>> = MutableLiveData()
    val paymentInfo: LiveData<Resource<Response<ResponseBody>>>
        get() = _paymentInfo

    // Payment post token
    private val _paymentToken: MutableLiveData<Resource<Response<ResponseBody>>> = MutableLiveData()
    val paymentToken: LiveData<Resource<Response<ResponseBody>>>
        get() = _paymentToken

    fun getCourses(token: String) = viewModelScope.launch {
        _paymentToken.value = Resource.Loading
        _paymentToken.value = repository.paymentToken(token)
    }
}