package com.novashen.riverside.module.message.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.SystemMsgBean

/**
 * Created by sca_tl at 2023/2/20 10:23
 */
interface SystemMsgView: BaseView {
    fun onGetSystemMsgSuccess(systemMsgBean: SystemMsgBean)
    fun onGetSystemMsgError(msg: String?)
}