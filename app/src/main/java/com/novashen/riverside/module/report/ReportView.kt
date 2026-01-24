package com.novashen.riverside.module.report

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.ReportBean

/**
 * Created by sca_tl on 2022/12/16 14:56
 */
interface ReportView: BaseView {
    fun onReportSuccess(reportBean: ReportBean)
    fun onReportError(msg: String?)
}