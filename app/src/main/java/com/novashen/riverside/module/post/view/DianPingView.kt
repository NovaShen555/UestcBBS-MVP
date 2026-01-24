package com.novashen.riverside.module.post.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.PostDianPingBean

/**
 * Created by sca_tl at 2023/4/13 9:32
 */
interface DianPingView: BaseView {
    fun onGetPostDianPingListSuccess(commentBean: PostDianPingBean)
    fun onGetPostDianPingListError(msg: String?)
}