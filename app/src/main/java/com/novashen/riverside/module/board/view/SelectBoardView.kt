package com.novashen.riverside.module.board.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.CommonPostBean
import com.novashen.riverside.entity.ForumListBean
import com.novashen.riverside.entity.SubForumListBean

/**
 * Created by sca_tl at 2023/3/31 10:51
 */
interface SelectBoardView: BaseView {
    fun onGetMainBoardListSuccess(forumListBean: ForumListBean)
    fun onGetMainBoardListError(msg: String?)
    fun onGetChildBoardListSuccess(subForumListBean: SubForumListBean)
    fun onGetChildBoardListError(msg: String?)
    fun onGetClassificationSuccess(classifications: List<CommonPostBean.ClassificationTypeListBean>)
    fun onGetClassificationError(msg: String?, classifications: List<CommonPostBean.ClassificationTypeListBean>)
}