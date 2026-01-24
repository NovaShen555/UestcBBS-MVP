package com.novashen.riverside.module.post.view

import android.os.Bundle
import android.text.TextUtils
import com.novashen.riverside.R
import com.novashen.riverside.base.BaseVBBottomFragment
import com.novashen.riverside.databinding.FragmentViewOriginCommentBinding
import com.novashen.riverside.entity.ContentViewBean
import com.novashen.riverside.entity.PostDetailBean
import com.novashen.riverside.module.post.adapter.PostContentAdapter
import com.novashen.riverside.module.post.presenter.ViewOriginCommentPresenter
import com.novashen.riverside.util.Constant
import com.novashen.riverside.util.JsonUtil
import com.novashen.riverside.util.TimeUtil
import com.novashen.riverside.util.load

/**
 * Created by sca_tl on 2022/12/16 16:38
 */
class ViewOriginCommentFragment: BaseVBBottomFragment<ViewOriginCommentPresenter, ViewOriginCommentView, FragmentViewOriginCommentBinding>(), ViewOriginCommentView {

    private lateinit var mData: PostDetailBean.ListBean
    private var mTopicId: Int = Int.MAX_VALUE

    companion object {
        fun getInstance(bundle: Bundle?) = ViewOriginCommentFragment().apply {
            arguments = bundle
        }
    }

    override fun getViewBinding() = FragmentViewOriginCommentBinding.inflate(layoutInflater)

    override fun initPresenter() = ViewOriginCommentPresenter()

    override fun getBundle(bundle: Bundle?) {
        bundle?.let {
            mData = bundle.getSerializable(Constant.IntentKey.DATA_1) as PostDetailBean.ListBean
            mTopicId = bundle.getInt(Constant.IntentKey.TOPIC_ID)
        }
    }

    override fun initView() {
        val postContentAdapter = PostContentAdapter(requireContext(), mTopicId, null)
        val data = JsonUtil.modelListA2B(mData.reply_content, ContentViewBean::class.java, mData.reply_content.size)
        mBinding.recyclerView.adapter = postContentAdapter
        postContentAdapter.data = data
        postContentAdapter.type = PostContentAdapter.TYPE.VIEW_ORIGIN

        mBinding.avatar.load(mData.icon)
        mBinding.name.text = mData.reply_name
        mBinding.time.text = TimeUtil.formatTime(mData.posts_date, R.string.post_time1, context)
            .plus(" ").plus(if (TextUtils.isEmpty(mData.mobileSign)) "网页版" else mData.mobileSign.replace("来自", ""))
    }

    override fun setMaxHeightMultiplier() = 0.92
}