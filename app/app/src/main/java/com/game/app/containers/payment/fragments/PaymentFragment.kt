package com.game.app.containers.payment.fragments

import android.content.Intent
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.game.app.R
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.adapters.CategoryHighAdapter
import com.game.app.containers.home.adapters.FragmentDynamicAdapter
import com.game.app.containers.home.fragments.home.categories.CategoryLinearFragment
import com.game.app.containers.home.fragments.home.categories.CategoryVerticalFragment
import com.game.app.containers.home.models.HomeViewModel
import com.game.app.containers.payment.adapters.SwitchTariffFragmentAdapter
import com.game.app.containers.payment.models.PaymentViewModel
import com.game.app.containers.payment.tariffs.TariffMonthFragment
import com.game.app.containers.start.fragments.adapters.StartPageFragmentAdapter
import com.game.app.databinding.FragmentPaymentBinding
import com.game.app.models.category.CategoryModel
import com.game.app.models.content.ContentDataModel
import com.game.app.models.course.CourseDataModel
import com.game.app.models.error.ErrorModel
import com.game.app.models.payment.PaymentTariffModel
import com.game.app.network.Resource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.*
import com.game.app.utils.recycler.setAdapter
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PaymentFragment : BaseFragment<PaymentViewModel, FragmentPaymentBinding, UserRepository>() {
    private lateinit var adapter: SwitchTariffFragmentAdapter
    private var paymentTariffModel: PaymentTariffModel = PaymentTariffModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(!checkAuth("Для оформления подписки необходимо авторизоваться!")){
            return
        }

        runBlocking {
            viewModel.getPaymentTariffs()
        }

        binding.progressBarMain.visible(true)
        binding.buttonPayment.enable(false)

        binding.toolbar.setMarginTop(128)

        binding.toolbar.setNavigationOnClickListener {
            toHome()
        }

        binding.buttonPayment.setOnClickListener {
            navigation(R.id.action_paymentFragment_to_paymentSubscribeFragment)
        }

        viewModel.paymentTariff.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    if(it.value.isSuccessful){
                        lifecycleScope.launch {
                            paymentTariffModel = Gson().fromJson(
                                JsonParser.parseString(
                                    it.value.body()?.string()
                                ),
                                PaymentTariffModel::class.java
                            )

                            updateView(paymentTariffModel)
                        }
                    }else{
                        handleMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it) {
                        viewModel.getPaymentTariffs()
                    }
                }
                else -> {}
            }

            binding.progressBarMain.visible(false)
            binding.buttonPayment.enable(true)
        }
    }

    override fun getViewModel() = PaymentViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaymentBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() : UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences, true))
    }

    private fun updateView(tariff: PaymentTariffModel){
        val pager2 = binding.viewPager2
        val tabLayout = binding.tabLayout

        val fragments = ArrayMap<Int, Fragment>()
        tabLayout.removeAllTabs()

        for(i in 0 until tariff.tarrif.size){
            tabLayout.addTab(tabLayout.newTab(), i)

            fragments[i] = TariffMonthFragment(
                if(tariff.tarrif[i].period != 0) "1" else " ",
                tariff.tarrif[i].description!!,
                tariff.tarrif[i].price!!
            )
        }

        val fm: FragmentManager = requireActivity().supportFragmentManager
        adapter = SwitchTariffFragmentAdapter(fm, lifecycle, fragments)
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
    }
}