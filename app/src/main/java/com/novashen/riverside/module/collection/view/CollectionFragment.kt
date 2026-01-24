package com.novashen.riverside.module.collection.view

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.novashen.riverside.R
import com.novashen.riverside.base.BaseVBFragment
import com.novashen.riverside.databinding.FragmentCollectionBinding
import com.novashen.riverside.module.collection.adapter.CollectionPagerAdapter
import com.novashen.riverside.module.collection.presenter.CollectionPresenter
import com.novashen.util.ColorUtil
import com.novashen.util.desensitize

/**
 * Created by sca_tl at 2023/5/5 11:21
 */
class CollectionFragment: BaseVBFragment<CollectionPresenter, CollectionView, FragmentCollectionBinding>(), CollectionView {

    companion object {
        fun getInstance(bundle: Bundle?) = CollectionFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentCollectionBinding.inflate(layoutInflater)

    override fun initPresenter() = CollectionPresenter()

    override fun lazyLoad() {
        val titles = arrayOf("我的专辑", "推荐专辑", "所有专辑")
        mBinding.viewpager.apply {
            offscreenPageLimit = titles.size
            adapter = CollectionPagerAdapter(context as FragmentActivity)
            desensitize()
        }
        mBinding.tabLayout.setSelectedTabIndicatorColor(ColorUtil.getAlphaColor(0.65f, ColorUtil.getAttrColor(context, R.attr.colorPrimary)))
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}