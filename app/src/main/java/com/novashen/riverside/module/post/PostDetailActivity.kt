package com.novashen.riverside.module.post

import android.content.Intent
import android.os.Bundle
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.novashen.riverside.base.BaseVBActivity
import com.novashen.riverside.databinding.ActivityPostDetailBinding
import com.novashen.riverside.entity.FavoritePostResultBean
import com.novashen.riverside.entity.PostDetailBean
import com.novashen.riverside.entity.SupportResultBean
import com.novashen.riverside.entity.VoteResultBean
import com.novashen.riverside.module.post.presenter.NewPostDetailPresenter
import com.novashen.riverside.module.post.view.CommentFragment
import com.novashen.riverside.module.post.view.NewPostDetailView
import com.novashen.riverside.util.Constant

/**
 * Created by sca_tl at 2023/6/7 20:03
 */
class PostDetailActivity: BaseVBActivity<NewPostDetailPresenter, NewPostDetailView, ActivityPostDetailBinding>(), NewPostDetailView {

    private var topicId: Int = Int.MAX_VALUE
    private var postId: Int = Int.MAX_VALUE
    private var boardId: Int = Int.MAX_VALUE
    private var userId: Int = Int.MAX_VALUE
    private var locateComment: Bundle? = null
    private var pingjiaCount: Int = 0
    private var currentSort = CommentFragment.SORT.DEFAULT
    private var postDetailBean: PostDetailBean? = null
    private lateinit var postDetailAdapter: PostDetailAdapter

    override fun getIntent(intent: Intent?) {
        intent?.let {
            topicId = it.getIntExtra(Constant.IntentKey.TOPIC_ID, Int.MAX_VALUE)
            locateComment = it.getBundleExtra(Constant.IntentKey.LOCATE_COMMENT)
        }
    }

    override fun getViewBinding() = ActivityPostDetailBinding.inflate(layoutInflater)

    override fun initPresenter() = NewPostDetailPresenter()

    override fun initView(theftProof: Boolean) {
        super.initView(true)

        mPresenter?.getDetail(1, 0, 0, topicId, 0)
    }

    override fun onGetPostDetailSuccess(postDetailBean: PostDetailBean) {
        val detailEntity = mutableListOf<MultiItemEntity>()

        //头部数据
        detailEntity.add(postDetailBean)

        //帖子内容数据
        detailEntity.addAll(postDetailBean.topic.content)

        //投票
        postDetailBean.topic.poll_info?.let { poll ->
            detailEntity.add(poll)
        }

        //点赞，点踩，专辑，点评
        detailEntity.add(PostDetailAdapter.ExtraEntity())

        //tab + viewpager
        detailEntity.add(PostDetailAdapter.ViewPagerEntity())

        postDetailAdapter = PostDetailAdapter(getContext(), detailEntity)
        postDetailAdapter.postDetailBean = postDetailBean
        mBinding.recyclerView.adapter = postDetailAdapter
    }

    override fun onGetPostDetailError(msg: String?) {

    }

    override fun onVoteSuccess(voteResultBean: VoteResultBean) {

    }

    override fun onVoteError(msg: String?) {

    }

    override fun onFavoritePostSuccess(favoritePostResultBean: FavoritePostResultBean) {

    }

    override fun onFavoritePostError(msg: String?) {

    }

    override fun onSupportSuccess(supportResultBean: SupportResultBean, action: String, type: String) {

    }

    override fun onSupportError(msg: String?) {

    }

    override fun getContext() = this
}