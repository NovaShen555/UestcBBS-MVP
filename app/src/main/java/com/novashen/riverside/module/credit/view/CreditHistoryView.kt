package com.novashen.riverside.module.credit.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.MineCreditBean.CreditHistoryBean

interface CreditHistoryView: BaseView {
    fun onGetMineCreditHistorySuccess(creditHistoryBeans: List<CreditHistoryBean>, hasNext: Boolean)
    fun onGetMineCreditHistoryError(msg: String?)
}