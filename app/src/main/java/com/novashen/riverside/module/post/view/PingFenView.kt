package com.novashen.riverside.module.post.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.RateUserBean

/**
 * Created by sca_tl at 2023/4/20 16:28
 */
interface PingFenView: BaseView {
    fun onGetRateUserSuccess(rateUserBeans: List<RateUserBean>)
    fun onGetRateUserError(msg: String?)
}