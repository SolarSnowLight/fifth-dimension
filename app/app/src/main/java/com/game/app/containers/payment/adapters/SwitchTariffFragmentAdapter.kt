package com.game.app.containers.payment.adapters

import android.util.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.game.app.containers.base.BaseFragment

class SwitchTariffFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var fragments: ArrayMap<Int, Fragment>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return fragments[position]!!
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}