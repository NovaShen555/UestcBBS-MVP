package com.novashen.riverside.module.home.view;

import com.novashen.riverside.entity.BingPicBean;
import com.novashen.riverside.entity.CommonPostBean;
import com.novashen.riverside.entity.NoticeBean;

public interface LatestPostView {
    void getBannerDataSuccess(BingPicBean bingPicBean);
    void getSimplePostDataSuccess(CommonPostBean simplePostListBean);
    void getSimplePostDataError(String msg);
    void onGetNoticeSuccess(NoticeBean noticeBean);
    void onGetNoticeError(String msg);
    void onGetHomePageSuccess(String msg);
}
