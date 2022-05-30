package com.game.app.containers.home.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.models.ProfileViewModel
import com.game.app.databinding.FragmentProfileContainerBinding
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository

class ProfileContainerFragment : BaseFragment<ProfileViewModel, FragmentProfileContainerBinding, UserRepository>(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!checkAuth("Для просмотра информации о настройках необходимо авторизоваться")){
            return
        }
    }

    override fun getViewModel() = ProfileViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentProfileContainerBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences))
    }
}