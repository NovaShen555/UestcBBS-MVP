package com.novashen.riverside.module.board.model;

import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil;
import com.novashen.riverside.api.discourse.entity.CategoriesResponse;
import com.novashen.riverside.api.discourse.entity.CategoryDetailResponse;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

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
                .retryWhen(new Function<Observable<Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> apply(Observable<Throwable> errors) {
                        return errors
                                .zipWith(Observable.range(1, 3), new BiFunction<Throwable, Integer, Integer>() {
                                    @Override
                                    public Integer apply(Throwable throwable, Integer retryCount) {
                                        if (throwable instanceof HttpException
                                                && ((HttpException) throwable).code() == 429) {
                                            return retryCount;
                                        }
                                        throw Exceptions.propagate(throwable);
                                    }
                                })
                                .flatMap(new Function<Integer, Observable<?>>() {
                                    @Override
                                    public Observable<?> apply(Integer retryCount) {
                                        return Observable.timer(1, TimeUnit.SECONDS);
                                    }
                                });
                    }
                })
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
