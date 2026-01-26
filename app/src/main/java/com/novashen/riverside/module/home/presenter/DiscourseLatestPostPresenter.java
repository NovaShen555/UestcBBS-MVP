package com.novashen.riverside.module.home.presenter;

import android.content.Context;

import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.entity.CommonPostBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.home.model.DiscourseHomeModel;
import com.novashen.riverside.module.home.view.LatestPostView;

import io.reactivex.disposables.Disposable;

/**
 * Discourse 最新帖子 Presenter
 * 使用 Discourse API 获取帖子列表
 */
public class DiscourseLatestPostPresenter extends LatestPostPresenter {

    private DiscourseHomeModel discourseHomeModel = new DiscourseHomeModel();

    /**
     * 获取最新回复的帖子列表
     */
    public void getLatestTopics() {
        discourseHomeModel.getLatestTopics(new Observer<CommonPostBean>() {
            @Override
            public void OnSuccess(CommonPostBean commonPostBean) {
                if (commonPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.getSimplePostDataSuccess(commonPostBean);
                } else {
                    view.getSimplePostDataError("获取数据失败");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getSimplePostDataError(e.message);
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

    /**
     * 获取最新创建的帖子列表
     */
    public void getNewTopics() {
        discourseHomeModel.getNewTopics(new Observer<CommonPostBean>() {
            @Override
            public void OnSuccess(CommonPostBean commonPostBean) {
                if (commonPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.getSimplePostDataSuccess(commonPostBean);
                } else {
                    view.getSimplePostDataError("获取数据失败");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getSimplePostDataError(e.message);
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
