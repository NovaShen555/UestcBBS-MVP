package com.novashen.riverside.module.credit.view

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.novashen.riverside.R
import com.novashen.riverside.base.BaseVBBottomFragment
import com.novashen.riverside.databinding.FragmentWaterTaskBinding
import com.novashen.riverside.module.credit.adapter.WaterTaskPagerAdapter
import com.novashen.riverside.module.credit.presenter.WaterTaskPresenter
import com.novashen.util.ColorUtil
import com.novashen.util.desensitize

/**
 * Created by sca_tl at 2023/4/6 20:04
 */
class WaterTaskFragment: BaseVBBottomFragment<WaterTaskPresenter, WaterTaskView, FragmentWaterTaskBinding>(), WaterTaskView {

    companion object {
        fun getInstance(bundle: Bundle?) = WaterTaskFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentWaterTaskBinding.inflate(layoutInflater)

    override fun initView() {
        val titles = arrayOf("进行中", "新任务", "已完成", "已失败")
        mBinding.viewpager.apply {
            offscreenPageLimit = titles.size
            adapter = WaterTaskPagerAdapter(context as FragmentActivity)
            desensitize()
        }
        mBinding.tabLayout.setSelectedTabIndicatorColor(ColorUtil.getAlphaColor(0.65f, ColorUtil.getAttrColor(context, R.attr.colorPrimary)))
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    override fun initPresenter() = WaterTaskPresenter()

}