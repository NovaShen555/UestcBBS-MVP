package com.novashen.riverside.module.post.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.ViewWarningEntity

/**
 * created by sca_tl at 2023/5/2 15:16
 */
interface ViewWarningView: BaseView {
    fun onGetWarningDataSuccess(entity: ViewWarningEntity)
    fun onGetWarningDataError(msg: String?)
}