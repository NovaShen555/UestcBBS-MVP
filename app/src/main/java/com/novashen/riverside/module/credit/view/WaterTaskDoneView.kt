package com.novashen.riverside.module.credit.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.TaskBean

/**
 * created by sca_tl at 2023/4/7 19:37
 */
interface WaterTaskDoneView: BaseView {
    fun onGetDoneTaskSuccess(taskBeans: List<TaskBean>, formhash: String?)
    fun onGetDoneTaskError(msg: String?)
}