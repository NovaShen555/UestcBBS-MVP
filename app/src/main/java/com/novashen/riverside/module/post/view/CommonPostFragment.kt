package com.novashen.riverside.module.post.view

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.novashen.riverside.R
import com.novashen.riverside.annotation.ToastType
import com.novashen.riverside.base.BaseVBFragment
import com.novashen.riverside.callback.IHomeRefresh
import com.novashen.riverside.databinding.FragmentCommonPostBinding
import com.novashen.riverside.entity.CommonPostBean
import com.novashen.riverside.manager.ForumListManager
import com.novashen.riverside.module.board.view.BoardActivity
import com.novashen.riverside.module.post.adapter.CommonPostAdapter
import com.novashen.riverside.module.post.presenter.CommonPostPresenter
import com.novashen.riverside.module.user.view.UserDetailActivity
import com.novashen.riverside.util.Constant
import com.novashen.riverside.util.SharePrefUtil
import com.novashen.riverside.util.showToast
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * Created by sca_tl at 2023/4/26 10:08
 */
class CommonPostFragment: BaseVBFragment<CommonPostPresenter, CommonPostView, FragmentCommonPostBinding>(), CommonPostView, IHomeRefresh {

    private var mType: String = TYPE_BOARD_POST
    private var mUid: Int = Int.MAX_VALUE
    private var mUsername: String? = null // Add username field
    private var mPage: Int = 1
    private var mNoMoreData = false
    private lateinit var commonPostAdapter: CommonPostAdapter

    companion object {
        const val TYPE_USER_POST = "user_post"
        const val TYPE_USER_REPLY = "user_reply"
        const val TYPE_USER_FAVORITE = "user_favorite"
        const val TYPE_BOARD_POST = "board_post"
        const val TYPE_HOT_POST = "hot_post"
        const val TYPE_ESSENCE_POST = "essence_post"
        const val TYPE_NEW_REPLY_POST = "new_reply_post"

        fun getInstance(bundle: Bundle?) = CommonPostFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentCommonPostBinding.inflate(layoutInflater)

    override fun initPresenter(): CommonPostPresenter {
        // 如果是最新回复类型，使用 Discourse Presenter
        return if (mType == TYPE_NEW_REPLY_POST) {
            DiscourseCommonPostPresenter(this)
        } else if (mType == TYPE_USER_POST || mType == TYPE_USER_REPLY) {
             // For user detail lists
             CommonPostPresenter() 
        } else {
            CommonPostPresenter()
        }
    }

    // Discourse 帖子 Presenter，用于最新回复
    private class DiscourseCommonPostPresenter(
        private val fragment: CommonPostFragment
    ) : CommonPostPresenter() {
        private val discoursePresenter = com.novashen.riverside.module.home.presenter.DiscourseLatestPostPresenter()

        init {
            discoursePresenter.attachView(object : com.novashen.riverside.module.home.view.LatestPostView {
                override fun getSimplePostDataSuccess(simplePostListBean: CommonPostBean) {
                    fragment.onGetPostSuccess(simplePostListBean)
                }

                override fun getSimplePostDataError(msg: String?) {
                    fragment.onGetPostError(msg)
                }

                override fun getBannerDataSuccess(bingPicBean: com.novashen.riverside.entity.BingPicBean?) {}
                override fun onGetNoticeSuccess(noticeBean: com.novashen.riverside.entity.NoticeBean?) {}
                override fun onGetNoticeError(msg: String?) {}
                override fun onGetHomePageSuccess(msg: String?) {}
            })
        }

        fun getLatestTopics() {
            discoursePresenter.getLatestTopics()
        }
    }

    override fun getBundle(bundle: Bundle?) {
        mType = bundle?.getString(Constant.IntentKey.TYPE, TYPE_BOARD_POST)?: TYPE_BOARD_POST
        mUid = bundle?.getInt(Constant.IntentKey.USER_ID, Int.MAX_VALUE)?: Int.MAX_VALUE
        // Try to get username from bundle, if not there, maybe we can fetch it or it's current user
        mUsername = bundle?.getString(Constant.IntentKey.USER_NAME)
        
        // If username is null but uid is current user, get from pref
        if (mUsername.isNullOrEmpty() && mUid == SharePrefUtil.getUid(context)) {
            mUsername = SharePrefUtil.getDiscourseUsername(context)
        }
    }

    override fun initView() {
        super.initView()
        commonPostAdapter = CommonPostAdapter(R.layout.item_common_post, mType, onPreload = {
            if (SharePrefUtil.isAutoLoadMore(context) && !mNoMoreData) {
                lazyLoad()
            }
        })
        mBinding.recyclerView.apply {
            adapter = commonPostAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
        }

        mBinding.statusView.loading()
    }

    override fun lazyLoad() {
        when(mType) {
            TYPE_USER_POST -> {
                if (!mUsername.isNullOrEmpty()) {
                    mPresenter?.userPostDiscourse(mUsername!!, TYPE_USER_POST, mPage)
                } else {
                    mPresenter?.userPost(mPage, SharePrefUtil.getPageSize(context), mUid, "topic")
                }
            }
            TYPE_USER_REPLY -> {
                 if (!mUsername.isNullOrEmpty()) {
                    mPresenter?.userPostDiscourse(mUsername!!, TYPE_USER_REPLY, mPage)
                } else {
                    mPresenter?.userPost(mPage, SharePrefUtil.getPageSize(context), mUid, "reply")
                }
            }
            TYPE_USER_FAVORITE -> {
                if (!mUsername.isNullOrEmpty() && mUid == SharePrefUtil.getUid(context)) {
                    mPresenter?.userBookmarksDiscourse(mUsername!!, mPage)
                } else if (!mUsername.isNullOrEmpty()) {
                    mPresenter?.userLikesDiscourse(mUsername!!, mPage)
                } else {
                    mPresenter?.userPost(mPage, SharePrefUtil.getPageSize(context), mUid, "favorite")
                }
            }
            TYPE_HOT_POST -> {
                mPresenter?.getHotPostList(mPage, SharePrefUtil.getPageSize(context))
            }
            TYPE_ESSENCE_POST -> {
                mPresenter?.getHomeTopicList(mPage, SharePrefUtil.getPageSize(context), "essence")
            }
            TYPE_NEW_REPLY_POST -> {
                // 使用 Discourse API 获取最新回复
                if (mPresenter is CommonPostPresenter && mPage == 1) {
                    try {
                        val method = mPresenter!!.javaClass.getDeclaredMethod("getLatestTopics")
                        method.invoke(mPresenter)
                    } catch (e: Exception) {
                        // 如果反射失败，使用原有 API
                        mPresenter?.getHomeTopicList(mPage, SharePrefUtil.getPageSize(context), "all")
                    }
                } else {
                    // 分页暂不支持，显示提示
                    mBinding.refreshLayout.finishLoadMore()
                    showToast("已加载全部内容", ToastType.TYPE_NORMAL)
                    mNoMoreData = true
                }
            }
        }
    }

    override fun setOnItemClickListener() {
        commonPostAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.board_name) {

                val parentBoardId = ForumListManager.INSTANCE.getParentForum(commonPostAdapter.data[position].board_id).id

                val intent = Intent(context, BoardActivity::class.java).apply {
                    putExtra(Constant.IntentKey.BOARD_ID, parentBoardId)
                    putExtra(Constant.IntentKey.LOCATE_BOARD_ID, commonPostAdapter.data[position].board_id)
                    putExtra(Constant.IntentKey.BOARD_NAME, commonPostAdapter.data[position].board_name)
                }

                startActivity(intent)
            }

            if (view.id == R.id.avatar) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, commonPostAdapter.data[position].user_id)
                    putExtra(Constant.IntentKey.USER_NAME, commonPostAdapter.data[position].user_nick_name)
                }
                startActivity(intent)
            }

            if (view.id == R.id.content_layout) {
                val intent = Intent(context, NewPostDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.TOPIC_ID, commonPostAdapter.data[position].topic_id)
                }
                startActivity(intent)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        lazyLoad()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (!commonPostAdapter.isPreloading) {
            lazyLoad()
        }
    }

    override fun onGetPostSuccess(commonPostBean: CommonPostBean) {
        commonPostAdapter.isPreloading = false
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            if (commonPostBean.list.isEmpty()) {
                mBinding.statusView.error("啊哦，这里空空的~")
            } else {
                commonPostAdapter.addData(commonPostBean.list, true)
                mBinding.recyclerView.scheduleLayoutAnimation()
            }
        } else {
            commonPostAdapter.addData(commonPostBean.list, false)
        }

        if (commonPostBean.has_next == 1) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mNoMoreData = true
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetPostError(msg: String?) {
        commonPostAdapter.isPreloading = false
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (commonPostAdapter.data.size != 0) {
                showToast(msg, ToastType.TYPE_ERROR)
            } else {
                mBinding.statusView.error(msg)
            }
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }

    override fun onRefresh() {
        mBinding.recyclerView.scrollToPosition(0)
        mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
    }
}