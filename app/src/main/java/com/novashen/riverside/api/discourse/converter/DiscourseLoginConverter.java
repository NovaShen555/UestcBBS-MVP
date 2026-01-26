package com.novashen.riverside.api.discourse.converter;

import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.api.discourse.entity.LoginResponse;
import com.novashen.riverside.entity.LoginBean;

/**
 * 将 Discourse 登录响应转换为 LoginBean
 */
public class DiscourseLoginConverter {

    /**
     * 将 Discourse LoginResponse 转换为 LoginBean
     */
    public static LoginBean convertToLoginBean(LoginResponse.User discourseUser) {
        LoginBean loginBean = new LoginBean();

        // 设置成功标记
        loginBean.rs = ApiConstant.Code.SUCCESS_CODE;
        loginBean.errcode = "";

        // 设置 head
        loginBean.head = new LoginBean.HeadBean();
        loginBean.head.errCode = "0";
        loginBean.head.errInfo = "";
        loginBean.head.version = "1.0";
        loginBean.head.alert = 0;

        // 设置 body
        loginBean.body = new LoginBean.BodyBean();
        loginBean.body.externInfo = new LoginBean.BodyBean.ExternInfoBean();
        loginBean.body.externInfo.padding = "";

        // 设置用户信息
        loginBean.uid = discourseUser.getId();
        loginBean.userName = discourseUser.getUsername();

        // 构建头像 URL
        if (discourseUser.getAvatarTemplate() != null) {
            loginBean.avatar = "https://river-side.cc" +
                discourseUser.getAvatarTemplate().replace("{size}", "120");
        } else {
            loginBean.avatar = "";
        }

        loginBean.userTitle = discourseUser.getName() != null ? discourseUser.getName() : "";

        // Discourse 没有性别信息，默认设置
        loginBean.gender = 0;

        // Discourse 使用 trust_level，可以映射到 groupid
        loginBean.groupid = discourseUser.getTrustLevel();

        // Token 和 secret（Discourse 使用 Cookie 认证，这里设置为空）
        loginBean.token = "";
        loginBean.secret = "";

        // 其他字段
        loginBean.isValidation = 1;
        loginBean.score = 0;
        loginBean.mobile = "";

        return loginBean;
    }

    /**
     * 创建登录失败的 LoginBean
     */
    public static LoginBean createErrorLoginBean(String errorMsg) {
        LoginBean loginBean = new LoginBean();

        loginBean.rs = ApiConstant.Code.ERROR_CODE;
        loginBean.errcode = "-1";

        loginBean.head = new LoginBean.HeadBean();
        loginBean.head.errCode = "-1";
        loginBean.head.errInfo = errorMsg;
        loginBean.head.version = "1.0";
        loginBean.head.alert = 0;

        return loginBean;
    }
}
