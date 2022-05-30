package com.game.app.containers.home.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.game.app.containers.home.fragments.home.categories.deprecated.*

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return CategoryResourceFragment()
            1 -> return CategoryTimeFragment()
            2 -> return CategoryChildFragment()
            3 -> return CategoryDialogFragment()
            4 -> return CategoryChangeFragment()
            5 -> return CategoryMoneyFragment()
        }
        return CategoryTimeFragment()
    }

    override fun getItemCount(): Int {
        return 6
    }
}