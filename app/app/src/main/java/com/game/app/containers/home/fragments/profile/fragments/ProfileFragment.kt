package com.game.app.containers.home.fragments.profile.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.game.app.R
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.models.ProfileViewModel
import com.game.app.data.NotificationPreferences
import com.game.app.databinding.FragmentProfileBinding
import com.game.app.models.auth.AuthRegisterModel
import com.game.app.models.error.ErrorModel
import com.game.app.models.notification.NotificationModel
import com.game.app.models.user.UserDataModel
import com.game.app.models.user.UserInfoModel
import com.game.app.network.Resource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding, UserRepository>(){

    private lateinit var notificationPreferences: NotificationPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!checkAuth("Для просмотра информации о профиле необходимо авторизоваться")){
            return
        }

        val userAuthData = Gson().fromJson(
            runBlocking {
                userPreferences.authData.first()
            },
            UserDataModel::class.java
        )

        viewModel.getUserInfo(userAuthData.usersId)

        notificationPreferences = NotificationPreferences(requireContext())

        binding.switchTheme.enable(false)

        binding.settings.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("info_user", runBlocking {
                userPreferences.userInfoData.first()
            })

            navigation(
                R.id.action_profileFragment2_to_settingsFragment2,
                bundle
            )
        }

        binding.switchNotification.isChecked = Gson().fromJson(
            runBlocking {
                notificationPreferences.notification.first()
            },
            NotificationModel::class.java
        ).isReceiveNotification

        binding.switchNotification.setOnClickListener {
            runBlocking {
                notificationPreferences.saveNotification(
                    Gson().toJson(
                        NotificationModel(
                            isReceiveNotification = binding.switchNotification.isChecked
                        )
                    )
                )
            }
        }

        userPreferences.userInfoData.asLiveData().observe(requireActivity()){
            if(it != null){
                val data = Gson().fromJson(it, UserInfoModel::class.java)

                binding.tvNameProfile.text = data.name
                binding.tvSurnameProfile.text = data.surname
            }
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

        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val authData = runBlocking {
                    userPreferences.authData.first()
                }

                if(authData != null){
                    val userInfo = runBlocking {
                        userPreferences.userInfoData.first()
                    }

                    if(userInfo == null){
                        val authDataObj = Gson().fromJson(authData, UserDataModel::class.java)
                        viewModel.getUserInfo(authDataObj.usersId)
                    }
                }
            }
        }

        binding.buttonLogout.setOnClickListener{
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Выход из аккаунта")
                .setMessage("Вы уверены что хотите выйти из аккаунта?")
                .setPositiveButton("Да"){ dialog, id ->
                    logout()
                }
                .setNegativeButton("Нет"){dialog, id ->
                    dialog.dismiss()
                }

            alertDialog.show()
        }
    }

    override fun getViewModel() = ProfileViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentProfileBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences))
    }
}