package com.game.app.containers.home.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.game.app.containers.home.fragments.note.categories.deprecated.*

class FragmentVerticalAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return CategoryResourceVFragment()
            1 -> return CategoryTimeVFragment()
            2 -> return CategoryChildVFragment()
            3 -> return CategoryDialogVFragment()
            4 -> return CategoryChangeVFragment()
            5 -> return CategoryMoneyVFragment()
        }
        return CategoryTimeVFragment()
    }

    override fun getItemCount(): Int {
        return 6
    }
}