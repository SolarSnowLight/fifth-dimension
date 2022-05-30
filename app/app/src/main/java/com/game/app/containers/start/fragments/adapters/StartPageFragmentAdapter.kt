package com.game.app.containers.start.fragments.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.game.app.containers.home.fragments.home.categories.*
import com.game.app.containers.start.fragments.pages.PageStartOneFragment
import com.game.app.containers.start.fragments.pages.PageStartThreeFragment
import com.game.app.containers.start.fragments.pages.PageStartTwoFragment

class StartPageFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return PageStartOneFragment()
            1 -> return PageStartTwoFragment()
            2 -> return PageStartThreeFragment()
        }
        return PageStartOneFragment()
    }

    override fun getItemCount(): Int {
        return 3
    }
}