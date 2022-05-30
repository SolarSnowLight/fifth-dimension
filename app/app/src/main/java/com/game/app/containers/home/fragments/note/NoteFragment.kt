package com.game.app.containers.home.fragments.note

import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.asLiveData
import androidx.viewpager2.widget.ViewPager2
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.adapters.FragmentDynamicAdapter
import com.game.app.containers.home.fragments.note.categories.CategoryNoteFragment
import com.game.app.containers.home.models.HomeViewModel
import com.game.app.data.CategoryPreferences
import com.game.app.databinding.FragmentNoteBinding
import com.game.app.models.category.CategoryModel
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.visible
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson

class NoteFragment : BaseFragment<HomeViewModel, FragmentNoteBinding, UserRepository>(){
    private lateinit var adapter: FragmentDynamicAdapter
    private lateinit var categoryPreferences: CategoryPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!checkAuth("Для просмотра информации о закреплённых курсах необходимо авторизоваться")){
            return
        }

        categoryPreferences = CategoryPreferences(requireContext())
        binding.progressBarNote.visible(false)

        val fm: FragmentManager = requireActivity().supportFragmentManager
        val pager2 = binding.viewPager2
        val tabLayout = binding.tabLayout

        categoryPreferences.categories.asLiveData().observe(requireActivity()){
            if(it != null){
                val categoryData = Gson().fromJson(
                    it,
                    CategoryModel::class.java
                )

                viewModel.getCoursesByTitle(categoryData.categories.first().title.toString())

                val fragments = ArrayMap<Int, BaseFragment<*, *, *>>()
                tabLayout.removeAllTabs()

                for(i in 0 until categoryData.categories.size){
                    tabLayout.addTab(tabLayout.newTab().setText(categoryData.categories[i].title), i)
                    fragments[i] = CategoryNoteFragment(categoryData.categories[i])
                }

                adapter = FragmentDynamicAdapter(fm, lifecycle, fragments)

                pager2.adapter = adapter
                pager2.isUserInputEnabled = false

                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        pager2.setCurrentItem(tab.position, true)
                        adapter.notifyItemChanged(tab.position)
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

        /*viewModel.user.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    updateUI(it.value)
                    binding.progressBarHome.visible(false)
                }
                is Resource.Loading -> {
                    binding.progressBarHome.visible(true)
                }
                else -> {}
            }
        })*/


    }

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNoteBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences))
    }
}