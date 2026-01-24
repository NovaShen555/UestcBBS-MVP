package com.novashen.riverside.module.account.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.LoginBean

/**
 * Created by sca_tl at 2023/6/2 14:11
 */
interface LoginView: BaseView {
    fun onLoginSuccess(loginBean: LoginBean)
    fun onLoginError(msg: String?)
}