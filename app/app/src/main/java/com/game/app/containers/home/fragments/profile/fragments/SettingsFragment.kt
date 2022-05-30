package com.game.app.containers.home.fragments.profile.fragments

import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.game.app.R
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.models.ProfileViewModel
import com.game.app.databinding.FragmentSettingsBinding
import com.game.app.models.error.ErrorModel
import com.game.app.models.user.UserDataModel
import com.game.app.models.user.UserInfoModel
import com.game.app.network.Resource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SettingsFragment : BaseFragment<ProfileViewModel, FragmentSettingsBinding, UserRepository>(){

    private lateinit var infoUserData: UserInfoModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!checkAuth("Для просмотра информации о настройках необходимо авторизоваться")){
            return
        }

        // activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        infoUserData = Gson().fromJson(
            arguments?.getString("info_user"),
            UserInfoModel::class.java
        )

        binding.editTextName.setText(infoUserData.name)
        binding.editTextSurname.setText(infoUserData.surname)
        binding.editTextEmail.setText(infoUserData.email)

        binding.toolbar.setMarginTop(128)

        binding.toolbar.setNavigationOnClickListener {
            navigation(R.id.action_settingsFragment2_to_profileFragment2)
        }

        binding.buttonSave.enable(false)

        binding.editTextPassword.addTextChangedListener {
            if(binding.editTextPassword.text!!.isNotEmpty()){
                binding.textInputLayoutRetry.visibility = View.VISIBLE
                binding.retryPassword.visibility = View.VISIBLE
                binding.buttonSave.enable(true)
            }else{
                binding.textInputLayoutRetry.visibility = View.GONE
                binding.retryPassword.visibility = View.GONE
                binding.retryPassword.text = ""
                binding.buttonSave.enable(false)
            }
        }

        binding.editTextPassword.setOnClickListener {
            binding.scroll.smoothScrollTo(0, binding.scroll.height)
        }

        binding.editTextPassword.setOnFocusChangeListener { view, b ->
            if(b){
                binding.scroll.smoothScrollTo(0, binding.scroll.height)
            }
        }

        binding.editTextName.addTextChangedListener {
            if(binding.editTextName.text.isNotEmpty()){
                binding.buttonSave.enable(true)
            }else{
                binding.buttonSave.enable(false)
            }
        }

        binding.editTextSurname.addTextChangedListener{
            if(binding.editTextSurname.text.isNotEmpty()){
                binding.buttonSave.enable(true)
            }else{
                binding.buttonSave.enable(false)
            }
        }

        binding.buttonSave.setOnClickListener {
            if(binding.editTextName.text.isEmpty()){
                handleErrorMessage("Необходимо ввести имя")
            }

            if(binding.editTextSurname.text.isEmpty()){
                handleErrorMessage("Необходимо ввести фамилию")
            }

            if(binding.editTextPassword.text!!.isNotEmpty()){
                if(binding.editTextPassword.text!!.length < 6){
                    handleErrorMessage("Длина пароля должна быть не менее 6-ти символов")
                    return@setOnClickListener
                }

                if(!binding.editTextPassword.text!!.contentEquals(binding.editTextRetryPassword.text)){
                    handleErrorMessage("Пароли не совпадают")
                }
            }

            val userAuthData = Gson().fromJson(
                runBlocking {
                    userPreferences.authData.first()
                },
                UserDataModel::class.java
            )

            viewModel.updateUserInfo(
                usersId = userAuthData.usersId,
                name = binding.editTextName.text.toString(),
                surname = binding.editTextSurname.text.toString(),
                password = if(binding.editTextPassword.text!!.isNotEmpty()) binding.editTextPassword.text.toString() else null
            )
        }

        viewModel.userInfo.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    when {
                        it.value.isSuccessful -> {
                            lifecycleScope.launch {
                                userPreferences.saveUserInfoData(
                                    Gson().toJson(
                                        JsonParser.parseString(
                                            it.value.body()?.string()
                                        )
                                    )
                                )
                            }

                            handleMessage("Информация о пользователе успешно изменена")
                            binding.buttonSave.enable(false)
                            binding.editTextPassword.setText("")
                            binding.textInputLayoutRetry.visibility = View.GONE
                            binding.retryPassword.visibility = View.GONE
                            binding.retryPassword.text = ""
                            binding.buttonSave.enable(false)
                        }
                        it.value.code() == 401 -> {
                            toAuth("Для просмотра информации о профиле необходимо авторизоваться")
                        }
                        else -> {
                            handleMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                        }
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it)
                }
                else -> {}
            }
        }
    }

    override fun getViewModel() = ProfileViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSettingsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences))
    }

    private fun equalString(a: String, b: String): Boolean{
        if(a.length != b.length){
            return false
        }

        for(i in a.indices){
            if(a[i] != b[i]){
                return false
            }
        }

        return true
    }
}