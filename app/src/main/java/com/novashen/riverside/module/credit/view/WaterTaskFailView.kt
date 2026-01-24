package com.novashen.riverside.module.credit.view

import com.novashen.riverside.entity.TaskBean

/**
 * created by sca_tl at 2023/4/7 20:29
 */
interface WaterTaskFailView: WaterTaskNewView {
    fun onGetFailedTaskSuccess(taskBeans: List<TaskBean>, formhash: String?)
    fun onGetFailedTaskError(msg: String?)
}