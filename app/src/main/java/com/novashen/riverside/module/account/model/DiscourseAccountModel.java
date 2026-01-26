package com.novashen.riverside.module.account.model;

import com.novashen.riverside.App;
import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil;
import com.novashen.riverside.api.discourse.entity.CsrfTokenResponse;
import com.novashen.riverside.api.discourse.entity.LoginResponse;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.util.SharePrefUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Discourse 账号模型
 * 用于处理 Discourse 登录
 */
public class DiscourseAccountModel {

    private DiscourseRetrofitUtil retrofitUtil;

    public DiscourseAccountModel() {
        retrofitUtil = DiscourseRetrofitUtil.getInstance();
    }

    /**
     * 登录到 Discourse
     * @param username 用户名
     * @param password 密码
     * @param observer 观察者
     */
    public void login(String username, String password, Observer<LoginResponse.User> observer) {
        // 步骤1: 获取 CSRF Token
        retrofitUtil.getApiService().getCsrfToken()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<CsrfTokenResponse>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        observer.OnDisposable(d);
                    }

                    @Override
                    public void onNext(CsrfTokenResponse csrfTokenResponse) {
                        String csrfToken = csrfTokenResponse.getCsrf();

                        // 步骤2: 设置 CSRF Token 并登录
                        retrofitUtil.getCsrfInterceptor().setManualCsrfToken(csrfToken);

                        retrofitUtil.getApiService().login(username, password, 1, "Asia/Shanghai")
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new io.reactivex.Observer<Response<LoginResponse>>() {
                                    @Override
                                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                                        // 已经在上面添加了
                                    }

                                    @Override
                                    public void onNext(Response<LoginResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            LoginResponse loginResponse = response.body();
                                            LoginResponse.User user = loginResponse.getUser();

                                            // 清除手动设置的 CSRF token
                                            retrofitUtil.getCsrfInterceptor().clearManualCsrfToken();

                                            // 保存用户凭证，用于自动重新登录
                                            SharePrefUtil.setDiscourseCredentials(App.getContext(), username, password);

                                            observer.OnSuccess(user);
                                        } else {
                                            observer.onError(new ExceptionHelper.ResponseThrowable(
                                                    new Exception("登录失败: " + response.code()),
                                                    ExceptionHelper.ERROR.HTTP_ERROR
                                            ));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        observer.onError(ExceptionHelper.handleException(e));
                                    }

                                    @Override
                                    public void onComplete() {
                                        observer.OnCompleted();
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(ExceptionHelper.handleException(e));
                    }

                    @Override
                    public void onComplete() {
                        // CSRF Token 获取完成
                    }
                });
    }

    /**
     * 清除会话
     */
    public void clearSession() {
        retrofitUtil.clearSession();
        // 同时清除保存的凭证
        SharePrefUtil.clearDiscourseCredentials(App.getContext());
    }
}
