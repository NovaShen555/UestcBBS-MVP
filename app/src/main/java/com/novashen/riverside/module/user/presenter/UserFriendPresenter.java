package com.novashen.riverside.module.user.presenter;

import android.content.Context;

import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.entity.UserFriendBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.user.model.UserModel;
import com.novashen.riverside.module.user.view.UserFriendView;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/5 16:42
 */
public class UserFriendPresenter extends BasePresenter<UserFriendView> {

    private UserModel userModel = new UserModel();

    public void getUserFriend(int uid, String type, Context context) {
        userModel.getUserFriend(1, 1000, uid, type, new Observer<UserFriendBean>() {
            @Override
            public void OnSuccess(UserFriendBean userFriendBean) {
                if (userFriendBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onGetUserFriendSuccess(userFriendBean);
                }

                if (userFriendBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onGetUserFriendError(userFriendBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUserFriendError(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
//                        SubscriptionManager.getInstance().add(d);
            }
        });
    }

}
