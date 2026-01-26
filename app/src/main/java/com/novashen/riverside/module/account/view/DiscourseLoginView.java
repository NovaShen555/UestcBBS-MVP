package com.novashen.riverside.module.account.view;

import com.novashen.riverside.api.discourse.entity.LoginResponse;

/**
 * Discourse 登录视图接口
 */
public interface DiscourseLoginView {

    /**
     * 登录成功
     * @param user 用户信息
     */
    void onLoginSuccess(LoginResponse.User user);

    /**
     * 登录失败
     * @param errorMsg 错误信息
     */
    void onLoginError(String errorMsg);
}
