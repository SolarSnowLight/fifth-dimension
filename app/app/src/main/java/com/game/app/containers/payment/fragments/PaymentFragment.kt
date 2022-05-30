package com.game.app.containers.payment.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.game.app.R
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.models.HomeViewModel
import com.game.app.containers.payment.adapters.SwitchTariffFragmentAdapter
import com.game.app.containers.start.fragments.adapters.StartPageFragmentAdapter
import com.game.app.databinding.FragmentPaymentBinding
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.navigation
import com.game.app.utils.setMarginTop
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.runBlocking

class PaymentFragment : BaseFragment<HomeViewModel, FragmentPaymentBinding, UserRepository>() {
    private lateinit var adapter: SwitchTariffFragmentAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(!checkAuth("Для оформления подписки необходимо авторизоваться!")){
            return
        }

        binding.toolbar.setMarginTop(128)

        binding.toolbar.setNavigationOnClickListener {
            toHome()
        }

        val pager2 = binding.viewPager2
        val tabLayout = binding.tabLayout

        val fm: FragmentManager = requireActivity().supportFragmentManager
        adapter = SwitchTariffFragmentAdapter(fm, lifecycle)
        pager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager2.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
                super.onPageSelected(position)
            }
        })

        binding.buttonPayment.setOnClickListener {
            navigation(R.id.action_paymentFragment_to_paymentSubscribeFragment)
        }
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
    }*/

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaymentBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() : UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences, false))
    }
}