package com.game.app.containers.home.fragments.profile.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.game.app.R
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.models.ProfileViewModel
import com.game.app.databinding.FragmentPolicyBinding
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.*


class PolicyFragment : BaseFragment<ProfileViewModel, FragmentPolicyBinding, UserRepository>(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!checkAuth("Для просмотра информации о настройках необходимо авторизоваться")){
            return
        }

        /*val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kristinakaruna.ru/5measurement/policy"))
        startActivity(browserIntent)*/

        binding.webView.settings.javaScriptEnabled = true

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                return true
            }
        }
        binding.webView.loadUrl("https://0eeb-109-194-30-250.ngrok.io/5measurement/payment")

        binding.toolbar.setMarginTop(128)

        binding.toolbar.setNavigationOnClickListener {
            navigation(R.id.action_policyFragment_to_profileFragment2)
        }
    }

    override fun getViewModel() = ProfileViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPolicyBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, null, false))
    }
}