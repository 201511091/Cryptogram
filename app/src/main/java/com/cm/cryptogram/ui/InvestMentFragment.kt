package com.cm.cryptogram.ui

import android.view.View
import com.cm.cryptogram.L
import com.cm.cryptogram.R
import com.cm.cryptogram.base.BaseFragment
import com.cm.cryptogram.databinding.FragmentInvestmentBinding
import com.cm.cryptogram.ui.tab.TabPagerAdapter
import com.google.android.material.tabs.TabLayout

internal class InvestMentFragment : BaseFragment<FragmentInvestmentBinding>() {
    override fun layoutRes(): Int = R.layout.fragment_investment
    private lateinit var pagerAdapter: TabPagerAdapter
    override fun onViewCreated() {
        L.e("::::::::::::::::::::::InvestMentFragment ")

        val keyword = preferenceHelper.keyWord
        L.e(":::키워드 " + keyword)

        keyword?.let {
            binding.tabLayout.visibility = View.VISIBLE
            binding.viewpager.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE

            binding.tabLayout.setupWithViewPager(binding.viewpager)
            pagerAdapter = TabPagerAdapter(childFragmentManager)

            binding.viewpager.run {
                adapter = pagerAdapter
                offscreenPageLimit = pagerAdapter.count
                currentItem = 0
            }
            binding.tabLayout.run {
                getTabAt(0)?.text = "NEWS"
                getTabAt(1)?.text = "SNS"
                getTabAt(2)?.text = "커뮤니티"
            }
            binding.tabLayout.addOnTabSelectedListener(tabLayoutOnPageChangeListener)
        }?: kotlin.run {
            binding.tabLayout.visibility = View.GONE
            binding.viewpager.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        }

    }

    private val tabLayoutOnPageChangeListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tabItem: TabLayout.Tab?) {}

        override fun onTabUnselected(tabItem: TabLayout.Tab?) {}

        override fun onTabSelected(tabItem: TabLayout.Tab?) {
            tabItem?.position?.let {
                binding.viewpager.currentItem = it
            }
        }
    }
}