package com.novashen.riverside.module.credit.view

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.novashen.riverside.R
import com.novashen.riverside.annotation.ToastType
import com.novashen.riverside.base.BaseEvent
import com.novashen.riverside.base.BaseVBFragment
import com.novashen.riverside.databinding.FragmentWaterTaskDoingBinding
import com.novashen.riverside.entity.TaskBean
import com.novashen.riverside.module.credit.adapter.WaterTaskAdapter
import com.novashen.riverside.module.credit.presenter.WaterTaskNewPresenter
import com.novashen.riverside.module.post.view.NewPostDetailActivity
import com.novashen.riverside.util.Constant
import com.novashen.riverside.util.showToast
import com.novashen.widget.dialog.BlurAlertDialogBuilder
import com.scwang.smart.refresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/4/7 17:49
 */
class WaterTaskNewFragment: BaseVBFragment<WaterTaskNewPresenter<WaterTaskNewView>, WaterTaskNewView, FragmentWaterTaskDoingBinding>(), WaterTaskNewView {

    private lateinit var mAdapter: WaterTaskAdapter

    companion object {
        fun getInstance(bundle: Bundle?) = WaterTaskNewFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {

    }

    override fun getViewBinding() = FragmentWaterTaskDoingBinding.inflate(layoutInflater)

    override fun initPresenter() = WaterTaskNewPresenter<WaterTaskNewView>()

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
        mPresenter?.getNewTaskList()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getNewTaskList()
    }

    override fun setOnItemClickListener() {
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when(view.id) {
                R.id.apply_task -> {
                    mPresenter?.applyNewTask(mAdapter.data[position].id, position)
                }
            }
        }
    }

    override fun onGetNewTaskSuccess(taskBeans: List<TaskBean>, formhash: String?) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()

        if (taskBeans.isEmpty()) {
            mBinding.statusView.error("啊哦，还没有新任务~")
        } else {
            mAdapter.setNewData(taskBeans)
            mBinding.recyclerView.scheduleLayoutAnimation()
        }
    }

    override fun onGetNewTaskError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMore()
        mBinding.statusView.error(msg)
    }

    override fun onApplyNewTaskSuccess(msg: String?, taskId: Int, position: Int) {
        mAdapter.data.removeAt(position)
        mAdapter.notifyItemRemoved(position)
        showToast(msg, ToastType.TYPE_SUCCESS)
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.APPLY_NEW_TASK_SUCCESS))
        if (taskId == 3) { //新手导航任务
            BlurAlertDialogBuilder(requireContext())
                .setMessage("检测到你申请了”新手导航回帖有礼“任务，需要在接下来的帖子里回复才能领取奖励！")
                .setTitle("跳转")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认跳转") { dialog, p1 ->
                    val intent = Intent(context, NewPostDetailActivity::class.java).apply {
                        putExtra(Constant.IntentKey.TOPIC_ID, 1821753)
                    }
                    context?.startActivity(intent)
                    dialog?.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onApplyNewTaskError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        if (baseEvent.eventCode == BaseEvent.EventCode.DELETE_TASK_SUCCESS) {
            mPresenter?.getNewTaskList()
        }
    }
}