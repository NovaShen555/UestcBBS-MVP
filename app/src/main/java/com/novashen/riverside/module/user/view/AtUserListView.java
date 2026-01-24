package com.novashen.riverside.module.user.view;

import com.novashen.riverside.base.BaseView;
import com.novashen.riverside.entity.AtUserListBean;
import com.novashen.riverside.entity.SearchUserBean;

public interface AtUserListView extends BaseView {
    void onGetAtUserListSuccess(AtUserListBean atUserListBean);
    void onGetAtUserListError(String msg);
    void onSearchUserSuccess(SearchUserBean searchUserBean);
    void onSearchUserError(String msg);
}
