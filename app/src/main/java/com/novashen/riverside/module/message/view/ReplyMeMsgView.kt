package com.novashen.riverside.module.message.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.ReplyMeMsgBean

/**
 * Created by sca_tl at 2023/2/17 10:05
 */
interface ReplyMeMsgView: BaseView {
    fun onGetReplyMeMsgSuccess(replyMeMsgBean: ReplyMeMsgBean)
    fun onGetReplyMeMsgError(msg: String?)
}