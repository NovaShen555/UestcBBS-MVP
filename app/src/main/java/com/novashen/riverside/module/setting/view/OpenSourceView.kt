package com.novashen.riverside.module.setting.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.OpenSourceBean

/**
 * Created by sca_tl at 2023/6/6 16:16
 */
interface OpenSourceView: BaseView {
    fun onGetOpenSourceDataSuccess(data: List<OpenSourceBean>)
    fun onGetOpenSourceDataError(msg: String?)
}