package com.novashen.riverside.module.home.presenter;

import android.content.Context;

import com.novashen.riverside.App;
import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.entity.BingPicBean;
import com.novashen.riverside.entity.CommonPostBean;
import com.novashen.riverside.entity.NoticeBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.home.model.HomeModel;
import com.novashen.riverside.module.home.view.LatestPostView;
import com.novashen.riverside.util.SharePrefUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.disposables.Disposable;

public class LatestPostPresenter extends BasePresenter<LatestPostView> {

    private HomeModel homeModel = new HomeModel();

    public void getBannerData() {
        homeModel.getBannerData(new Observer<BingPicBean>() {
            @Override
            public void OnSuccess(BingPicBean bingPicBean) {
                view.getBannerDataSuccess(bingPicBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {

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

    public void getSimplePostList(int page, int pageSize, String sortby, Context context){
        homeModel.getSimplePostList(page, pageSize, 0, sortby, new Observer<CommonPostBean>() {
            @Override
            public void OnSuccess(CommonPostBean simplePostListBean) {
                if (simplePostListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.getSimplePostDataSuccess(simplePostListBean);
                }
                if (simplePostListBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.getSimplePostDataError(simplePostListBean.head.errInfo);
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

    public void getNotice() {
        homeModel.getNotice(new Observer<NoticeBean>() {
            @Override
            public void OnSuccess(NoticeBean noticeBean) {
                view.onGetNoticeSuccess(noticeBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetNoticeError(e.message);
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

    public void getHomePage() {
        homeModel.getOnLineUSer(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");
                    SharePrefUtil.setForumHash(App.getContext(), formHash);
                } catch (Exception e) { }

                view.onGetHomePageSuccess(s);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) { }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

}
