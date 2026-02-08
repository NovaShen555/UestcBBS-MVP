package com.novashen.riverside.module.board.model;

import com.novashen.riverside.api.discourse.DiscourseApiHelper;
import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil;
import com.novashen.riverside.entity.CommonPostBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Discourse 板块帖子数据模型
 */
public class DiscourseBoardPostModel {

    private final DiscourseApiHelper apiHelper;

    public DiscourseBoardPostModel() {
        DiscourseRetrofitUtil retrofitUtil = DiscourseRetrofitUtil.getInstance();
        apiHelper = new DiscourseApiHelper(retrofitUtil.getApiService());
    }

    /**
     * 获取板块帖子列表（父板块ID + 子板块ID）
     */
    public void getCategoryTopics(int parentId, int childId, int page, Observer<CommonPostBean> observer) {
        apiHelper.getCategoryTopicsAsCommonPost(parentId, childId, page)
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
