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
import com.game.app.databinding.FragmentRegisterBinding
import com.game.app.models.error.ErrorModel
import com.game.app.models.notification.NotificationModel
import com.game.app.network.Resource
import com.game.app.network.apis.AuthApi
import com.game.app.repositories.AuthRepository
import com.game.app.utils.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment<AuthViewModel, FragmentRegisterBinding, AuthRepository>() {

    private lateinit var notificationPreferences: NotificationPreferences

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        notificationPreferences = NotificationPreferences(requireContext())

        binding.progressBar.visible(false)
        binding.buttonRegister.enable(false)

        viewModel.registerResponse.observe(viewLifecycleOwner) {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    if (it.value.isSuccessful) {
                        lifecycleScope.launch {
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
                        hideKeyboard()
                        handleErrorMessage(
                            Gson().fromJson(
                                it.value.errorBody()?.string().toString(),
                                ErrorModel::class.java
                            ).message!!
                        )
                    }
                }

                is Resource.Failure -> {
                    hideKeyboard()
                    handleApiError(it) {
                        register()
                    }
                }

                else -> {}
            }
        }

        binding.editTextPasswordRegister.addTextChangedListener{
            val email = binding.editTextEmailRegister.text.toString().trim()
            val password = binding.editTextPasswordRegister.text.toString()
            val name = binding.editTextNameRegister.text.toString().trim()
            val surname = binding.editTextSurnameRegister.text.toString().trim()

            binding.buttonRegister.enable(
                email.isNotEmpty() && password.isNotEmpty()
                        && name.isNotEmpty() && surname.isNotEmpty()
            )
        }

        binding.buttonRegister.setOnClickListener {
            register()
        }

        binding.linearLayoutLinkToLogin.setOnClickListener {
            navigation(R.id.action_registerFragment_to_loginFragment)
        }
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRegisterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = AuthRepository(remoteDataSource.buildApi(AuthApi::class.java, null, false), userPreferences)

    private fun register(){
        val email = binding.editTextEmailRegister.text.toString().trim()
        val password = binding.editTextPasswordRegister.text.toString()
        val name = binding.editTextNameRegister.text.toString().trim()
        val surname = binding.editTextSurnameRegister.text.toString().trim()

        viewModel.register(email, password, name, surname)
    }
}