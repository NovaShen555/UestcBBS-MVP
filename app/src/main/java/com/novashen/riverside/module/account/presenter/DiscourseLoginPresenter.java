package com.novashen.riverside.module.account.presenter;

import android.content.Context;

import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.api.discourse.entity.LoginResponse;
import com.novashen.riverside.base.BaseVBPresenter;
import com.novashen.riverside.entity.LoginBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.account.model.DiscourseAccountModel;
import com.novashen.riverside.module.account.view.LoginView;

import io.reactivex.disposables.Disposable;

/**
 * Discourse 登录 Presenter
 * 适配 LoginView 接口
 */
public class DiscourseLoginPresenter extends BaseVBPresenter<LoginView> {

    private DiscourseAccountModel discourseAccountModel = new DiscourseAccountModel();

    /**
     * 登录
     * @param context 上下文
     * @param username 用户名
     * @param password 密码
     */
    public void login(Context context, String username, String password) {
        discourseAccountModel.login(username, password, new Observer<LoginResponse.User>() {
            @Override
            public void OnSuccess(LoginResponse.User user) {
                // 转换为 LoginBean
                LoginBean loginBean = new LoginBean();
                loginBean.rs = ApiConstant.Code.SUCCESS_CODE;
                loginBean.uid = user.getId();
                loginBean.userName = user.getUsername();
                loginBean.userTitle = user.getName() != null ? user.getName() : "";
                loginBean.avatar = ""; // Discourse 头像需要单独处理

                LoginBean.BodyBean bodyBean = new LoginBean.BodyBean();
                loginBean.body = bodyBean;

                if (getMView() != null) {
                    getMView().onLoginSuccess(loginBean);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                if (getMView() != null) {
                    getMView().onLoginError(e.message);
                }
            }

            @Override
            public void OnCompleted() {
            }

            @Override
            public void OnDisposable(Disposable d) {
                if (getMCompositeDisposable() != null) {
                    getMCompositeDisposable().add(d);
                }
            }
        });
    }
}
