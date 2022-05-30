package com.game.app.containers.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.game.app.constants.data.value.CategoryTitleConstants
import com.game.app.containers.auth.AuthActivity
import com.game.app.containers.home.HomeActivity
import com.game.app.containers.payment.PaymentActivity
import com.game.app.data.UserPreferences
import com.game.app.models.error.ErrorModel
import com.game.app.models.user.UserDataModel
import com.game.app.network.RemoteDataSource
import com.game.app.network.Resource
import com.game.app.network.apis.AuthApi
import com.game.app.repositories.BaseRepository
import com.game.app.utils.handleApiError
import com.game.app.utils.handleMessage
import com.game.app.utils.startNewActivity
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
* Абстрактный класс для фрагментов (для поддержки архитектуры MVVM)
* */

abstract class BaseFragment<VM: BaseViewModel, B: ViewBinding, R: BaseRepository> : Fragment() {

    // Локальное хранилище пользовательских данных
    protected lateinit var userPreferences: UserPreferences

    // Класс ViewBinding, с помощью которого можно
    // осуществлять быстрый доступ к элементам вёрстки
    protected lateinit var binding : B

    // Ссылка на ViewModel, которая будет использована
    // для связывания данных и вёрстки
    protected lateinit var viewModel : VM

    // Инструмент для взаимодействия с сетью (формирует объект Retrofit)
    protected val remoteDataSource = RemoteDataSource()

    // Обработка одного из методов жизненного цикла фрагмента (создание вида)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Связывание пользовательских данных с экземпляром объекта UserPreferences
        userPreferences = UserPreferences(requireContext())

        // Связывание класса ViewBinding с конкретным экземпляром
        binding = getFragmentBinding(inflater, container)

        // Получение ViewModelFactory по определённому репозиторию
        val factory = ViewModelFactory(getFragmentRepository())

        // Получение ViewModel с помощью ViewModelFactory
        viewModel = ViewModelProvider(this, factory)[getViewModel()]

        // Запуск в рамках жизненного цикла получения
        // первого объекта из потока объектов Flow
        lifecycleScope.launch {
            userPreferences.authData.first()
        }

        // Возврат вида фрагмента
        return binding.root
    }

    abstract fun getViewModel() : Class<VM>
    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) : B
    abstract fun getFragmentRepository() : R

    // Реализация функции logout, для использования в других фрагментах
    // Запуск осуществляется в рамках жизненного цикла фрагмента
    // (иначе - в контексте жизненного цикла фрагмента)
    fun logout() = lifecycleScope.launch {
        // Получение первых данных из потока данных
        val data = userPreferences.authData.first()

        // Преобразование данных из JSON-формата в объект
        val dataObj = Gson().fromJson(data, UserDataModel::class.java)

        // Формирование API для взаимодействия с сервером
        val api = remoteDataSource.buildApi(AuthApi::class.java, userPreferences)

        // Вызов модели фрагмента (заранее установленного)
        // и передача ему созданного API
        viewModel.logout(
            dataObj.usersId,
            dataObj.tokens.accessToken,
            dataObj.tokens.refreshToken,
            dataObj.typeAuth,
            api
        )

        // Очистка пользовательских данных
        userPreferences.clear()

        // Открытие новой активности авторизации
        // requireActivity().startNewActivity(AuthActivity::class.java)
        requireActivity().startNewActivity(
            HomeActivity::class.java,
            "Вы вышли из своего аккаунта",
            "warning"
        )
    }

    fun checkAuth(message: String? = null, openAuthActivity: Boolean = true): Boolean{
        // Проверка на авторизацию пользователя
        val authDataModel = runBlocking {
            userPreferences.authData.first()
        }

        if((authDataModel == null) && openAuthActivity){
            requireActivity().startNewActivity(
                AuthActivity::class.java,
                message,
                "warning"
            )

            return false
        }

        return checkAuthDeep(message, openAuthActivity)
    }

    private fun checkAuthDeep(message: String? = null, openAuthActivity: Boolean = true): Boolean{
        // Получение первых данных из потока данных
        val data = runBlocking {
            userPreferences.authData.first()
        } ?: return false

        // Формирование API для взаимодействия с сервером
        val api = remoteDataSource.buildApi(AuthApi::class.java, userPreferences)

        val response = runBlocking {
            api.authVerification()
        }

        if(response.code() == 401){
            if(openAuthActivity){
                requireActivity().startNewActivity(
                    AuthActivity::class.java,
                    message,
                    "warning"
                )
            }

            return false
        }

        return true
    }

    fun toAuth(message: String? = null){
        requireActivity().startNewActivity(
            AuthActivity::class.java,
            message,
            "warning"
        )
    }

    fun toHome(message: String? = null){
        requireActivity().startNewActivity(
            HomeActivity::class.java,
            message,
            "warning"
        )
    }

    fun toPayment(){
        requireActivity().startNewActivity(
            PaymentActivity::class.java
        )
    }

    fun getAuthData(): UserDataModel?{
        if(!checkAuth(null, false)){
            return null
        }

        val authDataModel = runBlocking {
            userPreferences.authData.first()
        }

        return Gson().fromJson(
            authDataModel,
            UserDataModel::class.java
        )
    }
}