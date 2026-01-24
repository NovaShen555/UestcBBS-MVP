package com.novashen.riverside.module.post.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.*

/**
 * Created by sca_tl on 2022/12/5 10:56
 */
interface NewPostDetailView: BaseView {
    fun onGetPostDetailSuccess(postDetailBean: PostDetailBean)
    fun onGetPostDetailError(msg: String?)
    fun onVoteSuccess(voteResultBean: VoteResultBean)
    fun onVoteError(msg: String?)
    fun onFavoritePostSuccess(favoritePostResultBean: FavoritePostResultBean)
    fun onFavoritePostError(msg: String?)
    fun onSupportSuccess(supportResultBean: SupportResultBean, action: String, type: String)
    fun onSupportError(msg: String?)
}