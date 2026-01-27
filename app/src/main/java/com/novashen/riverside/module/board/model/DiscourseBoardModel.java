package com.novashen.riverside.module.board.model;

import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil;
import com.novashen.riverside.api.discourse.entity.CategoriesResponse;
import com.novashen.riverside.api.discourse.entity.CategoryDetailResponse;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Discourse 板块数据模型
 * 用于获取板块分类信息
 */
public class DiscourseBoardModel {

    /**
     * 获取所有板块分类
     */
    public void getCategories(Observer<CategoriesResponse> observer) {
        DiscourseRetrofitUtil.getInstance()
                .getApiService()
                .getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<CategoriesResponse>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        observer.OnDisposable(d);
                    }

                    @Override
                    public void onNext(CategoriesResponse response) {
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
     * 获取板块详情
     */
    public void getCategoryDetail(int categoryId, Observer<CategoryDetailResponse> observer) {
        DiscourseRetrofitUtil.getInstance()
                .getApiService()
                .getCategoryDetail(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<CategoryDetailResponse>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        observer.OnDisposable(d);
                    }

                    @Override
                    public void onNext(CategoryDetailResponse response) {
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
