package com.cm.cryptogram.ui.tab

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cm.cryptogram.base.BaseActivity.Companion.newInstance


class TabPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> newInstance(TabNewsFragment())
            1 -> newInstance(TabSnsFragment())
            2 -> newInstance(TabCommunityFragment())
            else -> throw IllegalAccessException()
        }
    }

    override fun getCount(): Int = PAGE_NUMBER

    companion object {
        private const val PAGE_NUMBER = 3
    }

}