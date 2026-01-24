package com.novashen.riverside.module.post.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.PostDetailBean

/**
 * Created by sca_tl at 2023/4/13 14:08
 */
interface DianZanView: BaseView {
    fun onGetPostDetailSuccess(postDetailBean: PostDetailBean)
    fun onGetPostDetailError(msg: String?)
}