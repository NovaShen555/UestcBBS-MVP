package com.novashen.riverside.module.board.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.ForumDetailBean
import com.novashen.riverside.entity.SubForumListBean

/**
 * Created by sca_tl at 2023/4/27 10:35
 */
interface BoardView: BaseView {
    fun onGetSubBoardListSuccess(subForumListBean: SubForumListBean)
    fun onGetSubBoardListError(msg: String?)
    fun onGetForumDetailSuccess(forumDetailBean: ForumDetailBean?)
}