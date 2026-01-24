package com.novashen.riverside.module.mine.view;

import com.novashen.riverside.entity.UserGroupBean;

public interface MineView {
    void onLoginOutSuccess();
    void onGetUserGroupSuccess(UserGroupBean userGroupBean);
    void onGetUserGroupError(String msg);
}
