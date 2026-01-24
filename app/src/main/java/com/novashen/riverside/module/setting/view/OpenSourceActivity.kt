package com.novashen.riverside.module.setting.view

import android.view.animation.AnimationUtils
import com.novashen.riverside.R
import com.novashen.riverside.base.BaseVBActivity
import com.novashen.riverside.databinding.ActivityOpenSourceBinding
import com.novashen.riverside.entity.OpenSourceBean
import com.novashen.riverside.module.setting.adapter.OpenSourceAdapter
import com.novashen.riverside.module.setting.presenter.OpenSourcePresenter
import com.novashen.riverside.util.CommonUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * Created by sca_tl at 2023/6/6 16:17
 */
class OpenSourceActivity: BaseVBActivity<OpenSourcePresenter, OpenSourceView, ActivityOpenSourceBinding>(), OpenSourceView {

    private lateinit var openSourceAdapter: OpenSourceAdapter

    override fun getViewBinding() = ActivityOpenSourceBinding.inflate(layoutInflater)

    override fun initPresenter() = OpenSourcePresenter()

    override fun initView(theftProof: Boolean) {
        super.initView(true)
        openSourceAdapter = OpenSourceAdapter(R.layout.item_open_source)
        mBinding.recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in)
        mBinding.recyclerView.adapter = openSourceAdapter
        mBinding.statusView.loading()
        mBinding.refreshLayout.setEnableLoadMore(false)
        mPresenter?.getOpenSourceData()
    }

    override fun setOnItemClickListener() {
        openSourceAdapter.setOnItemClickListener { adapter, view, position ->
            CommonUtil.openBrowser(getContext(), openSourceAdapter.data[position].link)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getOpenSourceData()
    }

    override fun onGetOpenSourceDataSuccess(data: List<OpenSourceBean>) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()
        openSourceAdapter.setNewData(data)
        mBinding.recyclerView.scheduleLayoutAnimation()
    }

    override fun onGetOpenSourceDataError(msg: String?) {
        mBinding.statusView.error(msg)
    }

    override fun getContext() = this
}