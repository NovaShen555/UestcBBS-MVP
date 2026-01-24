package com.novashen.riverside.module.home.view;

import com.novashen.riverside.entity.OnLineUserBean;

/**
 * author: sca_tl
 * date: 2020/10/7 11:25
 * description:
 */
public interface OnLineUserView {
    void onGetOnLineUserSuccess(OnLineUserBean onLineUserBean);
    void onGetOnLineUserError(String msg);
}
