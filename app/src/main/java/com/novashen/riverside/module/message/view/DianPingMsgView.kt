package com.novashen.riverside.module.message.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.DianPingMsgBean

/**
 * Created by sca_tl at 2023/2/17 10:28
 */
interface DianPingMsgView: BaseView {
    fun onGetDianPingMsgSuccess(dianPingMessageBean: DianPingMsgBean)
    fun onGetDianPingMsgError(msg: String?)
}