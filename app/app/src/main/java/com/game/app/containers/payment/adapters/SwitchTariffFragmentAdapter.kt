package com.game.app.containers.payment.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.game.app.containers.payment.tariffs.TariffMonthFragment
import com.game.app.containers.payment.tariffs.TariffWeekFragment
import com.game.app.containers.payment.tariffs.TariffYearFragment
import com.game.app.containers.start.fragments.pages.PageStartOneFragment

class SwitchTariffFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return TariffWeekFragment()
            1 -> return TariffMonthFragment()
            2 -> return TariffYearFragment()
        }
        return PageStartOneFragment()
    }

    override fun getItemCount(): Int {
        return 3
    }
}