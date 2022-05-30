package com.game.app.containers.home.adapters

import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.game.app.containers.base.BaseFragment

class FragmentDynamicAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var fragments: ArrayMap<Int, BaseFragment<*, *, *>>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return fragments[position]!!
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}