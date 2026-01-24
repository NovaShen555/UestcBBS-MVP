package com.novashen.riverside.module.message.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.AtMsgBean

/**
 * Created by sca_tl at 2023/2/17 14:06
 */
interface AtMeMsgView: BaseView {
    fun onGetAtMeMsgSuccess(atMsgBean: AtMsgBean)
    fun onGetAtMeMsgError(msg: String?)
}