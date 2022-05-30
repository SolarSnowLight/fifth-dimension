package com.game.app.containers.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.game.app.R
import com.game.app.containers.auth.models.AuthViewModel
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.HomeActivity
import com.game.app.data.NotificationPreferences
import com.game.app.databinding.FragmentLoginBinding
import com.game.app.models.error.ErrorModel
import com.game.app.models.notification.NotificationModel
import com.game.app.network.apis.AuthApi
import com.game.app.network.Resource
import com.game.app.repositories.AuthRepository
import com.game.app.utils.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

/*
* Фрагмент авторизации пользователя, который является наследником
* BaseFragment, с использованием ViewModel для авторизации, класс
* FragmentLoginBinding сгенерированный средой для быстрой связки с
* элементами вёрстки, а также AuthRepository, который предоставляет
* функционал для работы с данными
* */

class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {

    private lateinit var notificationPreferences: NotificationPreferences

    // Обработка одного из методов жизненного цикла фрагмента,
    // который обозначает что содержащая его активность вызвала
    // метод onCreate() и полностью создалась, таким образом
    // предоставив возможность использовать данные активности
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        notificationPreferences = NotificationPreferences(requireContext())

        // Первоначальная установка состояний компонентов
        binding.progressBar.visible(false)
        binding.buttonEnterLogin.enable(false)

        // Устанавливаем прослушивание на изменение данных loginResponse из viewModel
        // абстрактного класса (который был сгенерирован на основе переданного AuthViewModel)
        viewModel.loginResponse.observe(viewLifecycleOwner) {
            // Состояние компонента зависит от того, находится ли ресурс
            // в состоянии загрузки или нет
            binding.progressBar.visible(it is Resource.Loading)
            hideKeyboard()

            when (it) {
                // Обработка успешного сетевого взаимодействия
                is Resource.Success -> {
                    if (it.value.isSuccessful) {
                        // Запуск в контексте жизненного цикла
                        lifecycleScope.launch {
                            // Сохранение полученных данных в формате JSON-строки
                            viewModel.saveAuthData(
                                Gson().toJson(
                                    JsonParser.parseString(
                                        it.value.body()?.string()
                                    )
                                )
                            )

                            notificationPreferences.saveNotification(
                                Gson().toJson(
                                    NotificationModel(
                                        isReceiveNotification = true
                                    )
                                )
                            )
                            requireActivity().startNewActivity(HomeActivity::class.java)
                        }
                    } else {
                        handleErrorMessage(
                            Gson().fromJson(
                                it.value.errorBody()?.string().toString(),
                                ErrorModel::class.java
                            ).message!!
                        )
                    }
                }

                // Обработка ошибок связанные с сетью
                is Resource.Failure -> {
                    handleApiError(it) { login() }
                }
                else -> {}
            }
        }

        // Установка поведения на изменения текста в элементе управления
        binding.editTextPasswordLogin.addTextChangedListener {
            val email = binding.editTextEmailLogin.text.toString().trim()
            binding.buttonEnterLogin.enable(email.isNotEmpty() && it.toString().isNotEmpty())
        }

        binding.buttonEnterLogin.setOnClickListener {
            login()
        }

        binding.linearLayoutLinkToRegister.setOnClickListener {
            navigation(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = AuthRepository(remoteDataSource.buildApi(AuthApi::class.java, null, false), userPreferences)

    // Функционал элемента управления, который был выведен в отдельную функцию
    private fun login(){
        val email = binding.editTextEmailLogin.text.toString().trim()
        val password = binding.editTextPasswordLogin.text.toString()

        viewModel.login(email, password)
    }
}
