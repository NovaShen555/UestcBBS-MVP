package com.novashen.riverside.module.post.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.FavoritePostResultBean
import com.novashen.riverside.entity.PostDetailBean
import com.novashen.riverside.entity.VoteResultBean

/**
 * Created by sca_tl on 2022/12/5 10:56
 */
interface NewPostDetailView: BaseView {
    fun onGetPostDetailSuccess(postDetailBean: PostDetailBean)
    fun onGetPostDetailError(msg: String?)
    fun onVoteSuccess(voteResultBean: VoteResultBean)
    fun onVoteError(msg: String?)
    fun onBookmarkSuccess(bookmarked: Boolean, bookmarkId: Int)
    fun onBookmarkError(msg: String?)
    fun onSupportSuccess(action: String, reactionId: String)
    fun onSupportError(msg: String?)
}