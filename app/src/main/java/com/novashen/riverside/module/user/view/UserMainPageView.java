package com.novashen.riverside.module.user.view;

/**
 * author: sca_tl
 * date: 2021/3/17 13:02
 * description:
 */
public interface UserMainPageView {
    void onGetUserSpaceSuccess(String onLineTime, String registerTime, String lastLoginTime, String ipLocation);
    void onGetUserSpaceError(String msg);
}
