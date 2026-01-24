package com.novashen.riverside.module.user.presenter;

import android.content.Context;

import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.entity.AtUserListBean;
import com.novashen.riverside.entity.SearchUserBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.user.model.UserModel;
import com.novashen.riverside.module.user.view.AtUserListView;

import io.reactivex.disposables.Disposable;


public class AtUserListPresenter extends BasePresenter<AtUserListView> {

    private final UserModel userModel = new UserModel();

    //2好友  6关注
    public void getAtUSerList(int page, int pageSize) {
        userModel.getAtUserList(page, pageSize, new Observer<AtUserListBean>() {
            @Override
            public void OnSuccess(AtUserListBean atUserListBean) {
                if (atUserListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onGetAtUserListSuccess(atUserListBean);
                }
                if (atUserListBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onGetAtUserListError(atUserListBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetAtUserListError(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    public void searchUser(int page, int pageSize, String keyword, Context context) {
        userModel.searchUser(page, pageSize, 0, keyword,
                new Observer<SearchUserBean>() {
                    @Override
                    public void OnSuccess(SearchUserBean searchUserBean) {
                        if (searchUserBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onSearchUserSuccess(searchUserBean);
                        }
                        if (searchUserBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onSearchUserError(searchUserBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onSearchUserError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        disposable.add(d);
                    }
                });
    }

}
