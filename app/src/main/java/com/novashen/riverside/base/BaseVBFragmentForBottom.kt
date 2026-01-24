package com.novashen.riverside.base

import android.os.Bundle
import com.novashen.riverside.R
import com.novashen.riverside.databinding.FragmentForBottomBinding
import com.novashen.riverside.module.post.view.AddVoteFragment
import com.novashen.riverside.module.post.view.DianPingFragment
import com.novashen.riverside.module.post.view.PingFenFragment
import com.novashen.riverside.module.post.view.ViewWarningFragment
import com.novashen.riverside.util.Constant

/**
 * Created by sca_tl at 2023/4/12 20:15
 */
class BaseVBFragmentForBottom: BaseVBBottomFragment<BaseVBPresenter<BaseView>, BaseView, FragmentForBottomBinding>(), BaseView {

    private var mBusiness: String? = null
    private var mDraggable = true
    private var mMaxHeightMultiplier = 0.92
    private var mCancelable = true

    companion object {
        const val BIZ_DIANPING = "biz_dianping"
        const val BIZ_PINGFEN = "biz_pingfen"
        const val BIZ_VIEW_WARNING = "biz_view_warning"
        const val BIZ_ADD_VOTE = "biz_add_vote"

        fun getInstance(bundle: Bundle?) = BaseVBFragmentForBottom().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentForBottomBinding.inflate(layoutInflater)

    override fun getBundle(bundle: Bundle?) {
        bundle?.let {
            mBusiness = it.getString(Constant.IntentKey.TYPE, null)
            mDraggable = it.getBoolean(Constant.IntentKey.DRAGGABLE, true)
            mMaxHeightMultiplier = it.getDouble(Constant.IntentKey.MAX_HEIGHT_MULTIPLIER, 0.92)
            mCancelable = it.getBoolean(Constant.IntentKey.CANCELABLE, true)
        }
    }

    override fun initView() {

        isCancelable = mCancelable

        val fragment = when(mBusiness) {
            BIZ_DIANPING -> {
                mBinding.title.text = "点评列表"
                DianPingFragment.getInstance(arguments)
            }

            BIZ_PINGFEN -> {
                mBinding.title.text = "评分列表"
                PingFenFragment.getInstance(arguments)
            }

            BIZ_VIEW_WARNING -> {
                mBinding.title.text = "警告记录"
                ViewWarningFragment.getInstance(arguments)
            }

            BIZ_ADD_VOTE -> {
                mBinding.title.text = "添加投票"
                AddVoteFragment.getInstance(arguments)
            }

            else -> {
                mBinding.title.text = "无效的类型"
                mBinding.hint.text = "需要传入业务类型"
                null
            }
        }

        if (fragment != null) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    override fun initPresenter() = BaseVBPresenter<BaseView>()

    override fun setMaxHeightMultiplier() = mMaxHeightMultiplier

    override fun isDraggable() = mDraggable
}