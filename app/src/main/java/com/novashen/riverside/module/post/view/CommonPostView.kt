package com.novashen.riverside.module.post.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.CommonPostBean

/**
 * Created by sca_tl at 2023/4/26 10:09
 */
interface CommonPostView: BaseView {
    fun onGetPostSuccess(commonPostBean: CommonPostBean)
    fun onGetPostError(msg: String?)
}