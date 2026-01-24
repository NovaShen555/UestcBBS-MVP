package com.novashen.riverside.module.credit.view

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.novashen.riverside.R
import com.novashen.riverside.base.BaseVBFragment
import com.novashen.riverside.databinding.FragmentWaterTaskDoingBinding
import com.novashen.riverside.entity.TaskBean
import com.novashen.riverside.module.credit.adapter.WaterTaskAdapter
import com.novashen.riverside.module.credit.presenter.WaterTaskDonePresenter
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * created by sca_tl at 2023/4/7 19:37
 */
class WaterTaskDoneFragment: BaseVBFragment<WaterTaskDonePresenter, WaterTaskDoneView, FragmentWaterTaskDoingBinding>(), WaterTaskDoneView {

    private lateinit var mAdapter: WaterTaskAdapter

    companion object {
        fun getInstance(bundle: Bundle?) = WaterTaskDoneFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {

    }

    override fun getViewBinding() = FragmentWaterTaskDoingBinding.inflate(layoutInflater)

    override fun initPresenter() = WaterTaskDonePresenter()

    override fun initView() {
        super.initView()
        mAdapter = WaterTaskAdapter(R.layout.item_water_task_doing)
        mBinding.recyclerView.apply {
            adapter = mAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_top)
        }

        mBinding.statusView.loading()
    }

    override fun lazyLoad() {
        mPresenter?.getDoneTaskList()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getDoneTaskList()
    }

    override fun onGetDoneTaskSuccess(taskBeans: List<TaskBean>, formhash: String?) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()

        if (taskBeans.isEmpty()) {
            mBinding.statusView.error("啊哦，还没有已完成的任务~")
        } else {
            mAdapter.setNewData(taskBeans)
            mBinding.recyclerView.scheduleLayoutAnimation()
        }
    }

    override fun onGetDoneTaskError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMore()
        mBinding.statusView.error(msg)
    }
}