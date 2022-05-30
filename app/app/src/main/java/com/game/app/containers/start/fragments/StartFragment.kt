package com.game.app.containers.start.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.game.app.R
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.HomeActivity
import com.game.app.containers.home.adapters.FragmentAdapter
import com.game.app.containers.home.models.HomeViewModel
import com.game.app.containers.start.fragments.adapters.StartPageFragmentAdapter
import com.game.app.data.StartPreferences
import com.game.app.databinding.FragmentProfileBinding
import com.game.app.databinding.FragmentStartBinding
import com.game.app.models.user.UserDataModel
import com.game.app.models.user.UserOpenAppModel
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.startNewActivity
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Response

class StartFragment : BaseFragment<HomeViewModel, FragmentStartBinding, UserRepository>(){
    private lateinit var adapter: StartPageFragmentAdapter
    private lateinit var startPreferences: StartPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPreferences = StartPreferences(requireContext())

        val pager2 = binding.viewPager2
        val tabLayout = binding.tabLayout

        val fm: FragmentManager = requireActivity().supportFragmentManager
        adapter = StartPageFragmentAdapter(fm, lifecycle)
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

        binding.buttonNext.setOnClickListener {
            val nextTab = (tabLayout.selectedTabPosition + 1)
            if(tabLayout.tabCount <= nextTab){
                runBlocking {
                    startPreferences.saveFirstOpen(
                        Gson().toJson(
                            UserOpenAppModel(
                                firstOpen = false
                            )
                        )
                    )
                }

                requireActivity().startNewActivity(HomeActivity::class.java)
            }

            tabLayout.selectTab(tabLayout.getTabAt(if(tabLayout.tabCount <= nextTab) 0 else nextTab))
        }
    }

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentStartBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences))
    }
}