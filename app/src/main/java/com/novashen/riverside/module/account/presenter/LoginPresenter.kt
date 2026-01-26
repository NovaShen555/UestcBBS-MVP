package com.novashen.riverside.module.account.presenter

import android.content.Context
import com.alibaba.fastjson.JSONObject
import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil
import com.novashen.riverside.api.discourse.converter.DiscourseLoginConverter
import com.novashen.riverside.api.discourse.entity.CsrfTokenResponse
import com.novashen.riverside.api.discourse.entity.LoginResponse
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.LoginBean
import com.novashen.riverside.helper.ExceptionHelper.ResponseThrowable
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.module.account.model.AccountModel
import com.novashen.riverside.module.account.view.LoginView
import com.novashen.riverside.util.SharePrefUtil
import com.novashen.riverside.util.isNullOrEmpty
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * Created by sca_tl at 2023/6/2 14:11
 * Modified to support Discourse login
 */
class LoginPresenter: BaseVBPresenter<LoginView>() {

    private val accountModel = AccountModel()
    private val discourseRetrofitUtil = DiscourseRetrofitUtil.getInstance()

    // 切换标志：true 使用 Discourse，false 使用原有 API
    private val useDiscourse = true

    fun login(context: Context?, userName: String?, userPsw: String?) {
        if (useDiscourse) {
            loginWithDiscourse(context, userName, userPsw)
        } else {
            loginWithOriginalApi(context, userName, userPsw)
        }
    }

    /**
     * 使用 Discourse API 登录
     */
    private fun loginWithDiscourse(context: Context?, userName: String?, userPsw: String?) {
        // 步骤1: 获取 CSRF Token
        discourseRetrofitUtil.apiService.csrfToken
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(io.reactivex.schedulers.Schedulers.io())
            .subscribe(object : io.reactivex.Observer<CsrfTokenResponse> {
                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }

                override fun onNext(csrfTokenResponse: CsrfTokenResponse) {
                    val csrfToken = csrfTokenResponse.csrf

                    // 步骤2: 设置 CSRF Token 并登录
                    discourseRetrofitUtil.csrfInterceptor.setManualCsrfToken(csrfToken)

                    discourseRetrofitUtil.apiService.login(userName, userPsw, 1, "Asia/Shanghai")
                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                        .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                        .subscribe(object : io.reactivex.Observer<Response<LoginResponse>> {
                            override fun onSubscribe(d: Disposable) {
                                // 已经在上面添加了
                            }

                            override fun onNext(response: Response<LoginResponse>) {
                                if (response.isSuccessful && response.body() != null) {
                                    val loginResponse = response.body()!!
                                    val user = loginResponse.user

                                    // 清除手动设置的 CSRF token
                                    discourseRetrofitUtil.csrfInterceptor.clearManualCsrfToken()

                                    // 转换为 LoginBean
                                    val loginBean = DiscourseLoginConverter.convertToLoginBean(user)

                                    // 保存 Cookie（Discourse 使用 Cookie 认证）
                                    val cookies = response.headers().values("Set-Cookie")?.let { HashSet(it) }
                                    if (!cookies.isNullOrEmpty()) {
                                        SharePrefUtil.setCookies(context, cookies, userName)
                                        SharePrefUtil.setSuperAccount(context, true, userName)
                                    }

                                    mView?.onLoginSuccess(loginBean)
                                } else {
                                    val errorMsg = try {
                                        response.errorBody()?.string() ?: "登录失败"
                                    } catch (e: Exception) {
                                        "登录失败: ${response.code()}"
                                    }
                                    mView?.onLoginError("登录失败：$errorMsg")
                                }
                            }

                            override fun onError(e: Throwable) {
                                mView?.onLoginError("登录失败：${e.message}")
                            }

                            override fun onComplete() {
                                // 登录完成
                            }
                        })
                }

                override fun onError(e: Throwable) {
                    mView?.onLoginError("获取 CSRF Token 失败：${e.message}")
                }

                override fun onComplete() {
                    // CSRF Token 获取完成
                }
            })
    }

    /**
     * 使用原有 API 登录
     */
    private fun loginWithOriginalApi(context: Context?, userName: String?, userPsw: String?) {
        accountModel.login(userName, userPsw, object : Observer<Response<ResponseBody?>?>() {
            override fun OnSuccess(response: Response<ResponseBody?>?) {
                try {
                    if (response?.body() != null) {
                        val res = response.body()?.string()
                        val loginBean = JSONObject.parseObject(res, LoginBean::class.java)
                        if (loginBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            mView?.onLoginSuccess(loginBean)
                        } else {
                            mView?.onLoginError("登录失败：${loginBean.head.errInfo}")
                        }
                    }
                    val cookies = response?.headers()?.values("Set-Cookie")?.let { HashSet(it) }
                    if (!cookies.isNullOrEmpty()) {
                        SharePrefUtil.setCookies(context, cookies, userName)
                        SharePrefUtil.setSuperAccount(context, true, userName)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    mView?.onLoginError("登录失败：${e.message}")
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onLoginError("登录失败：${e.message}")
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}