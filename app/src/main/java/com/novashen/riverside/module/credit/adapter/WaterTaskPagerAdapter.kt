package com.novashen.riverside.module.credit.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.novashen.riverside.module.credit.view.WaterTaskDoingFragment
import com.novashen.riverside.module.credit.view.WaterTaskDoneFragment
import com.novashen.riverside.module.credit.view.WaterTaskFailedFragment
import com.novashen.riverside.module.credit.view.WaterTaskNewFragment

/**
 * Created by sca_tl at 2023/3/15 20:07
 */
class WaterTaskPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        fragments.apply {
            add(WaterTaskDoingFragment.getInstance(null))
            add(WaterTaskNewFragment.getInstance(null))
            add(WaterTaskDoneFragment.getInstance(null))
            add(WaterTaskFailedFragment.getInstance(null))
        }
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}