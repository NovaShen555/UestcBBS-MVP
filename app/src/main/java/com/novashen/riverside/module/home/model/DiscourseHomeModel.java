package com.novashen.riverside.module.home.model;

import com.novashen.riverside.api.discourse.DiscourseApiHelper;
import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil;
import com.novashen.riverside.entity.CommonPostBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Discourse 数据模型
 * 用于从 Discourse API 获取数据
 */
public class DiscourseHomeModel {

    private DiscourseApiHelper apiHelper;

    public DiscourseHomeModel() {
        DiscourseRetrofitUtil retrofitUtil = DiscourseRetrofitUtil.getInstance();
        apiHelper = new DiscourseApiHelper(retrofitUtil.getApiService());
    }

    /**
     * 获取最新回复的帖子列表
     * @param observer 观察者
     */
    public void getLatestTopics(Observer<CommonPostBean> observer) {
        apiHelper.getLatestTopicsAsCommonPost()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<CommonPostBean>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        observer.OnDisposable(d);
                    }

                    @Override
                    public void onNext(CommonPostBean commonPostBean) {
                        observer.OnSuccess(commonPostBean);
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
     * 获取最新创建的帖子列表
     * @param observer 观察者
     */
    public void getNewTopics(Observer<CommonPostBean> observer) {
        apiHelper.getNewTopicsAsCommonPost()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<CommonPostBean>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        observer.OnDisposable(d);
                    }

                    @Override
                    public void onNext(CommonPostBean commonPostBean) {
                        observer.OnSuccess(commonPostBean);
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
