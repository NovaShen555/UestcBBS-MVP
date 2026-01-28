package com.novashen.riverside.module.user.model;

import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil;
import com.novashen.riverside.api.discourse.entity.DiscourseUserResponse;
import com.novashen.riverside.api.discourse.entity.DiscourseUserSummaryResponse;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Discourse 用户信息 Model
 * 使用 Discourse API 获取用户信息
 */
public class DiscourseUserModel {

    /**
     * 获取用户信息
     * @param username 用户名
     * @param observer 观察者
     */
    public void getUserInfo(String username, Observer<DiscourseUserResponse> observer) {
        DiscourseRetrofitUtil.getInstance()
                .getApiService()
                .getUserInfo(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<DiscourseUserResponse>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        observer.OnDisposable(d);
                    }

                    @Override
                    public void onNext(DiscourseUserResponse response) {
                        observer.OnSuccess(response);
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

    /**
     * 获取用户摘要信息
     * @param username 用户名
     * @param observer 观察者
     */
    public void getUserSummary(String username, Observer<DiscourseUserSummaryResponse> observer) {
        DiscourseRetrofitUtil.getInstance()
                .getApiService()
                .getUserSummary(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<DiscourseUserSummaryResponse>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        observer.OnDisposable(d);
                    }

                    @Override
                    public void onNext(DiscourseUserSummaryResponse response) {
                        observer.OnSuccess(response);
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
}
