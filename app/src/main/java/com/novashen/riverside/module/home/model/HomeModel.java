package com.novashen.riverside.module.home.model;

import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.api.ApiService;
import com.novashen.riverside.entity.BingPicBean;
import com.novashen.riverside.entity.CommonPostBean;
import com.novashen.riverside.entity.NoticeBean;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.util.RetrofitCookieUtil;
import com.novashen.riverside.util.RetrofitUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeModel {
    public void getBannerData(Observer<BingPicBean> observer) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.BING_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(ApiService.class)
                .getBingPic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getSimplePostList(int page,
                                  int pageSize,
                                  int boardId,
                                  String sortby,
                                  Observer<CommonPostBean> observer) {
        Observable<CommonPostBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getHomeTopicList(page, pageSize, boardId, sortby);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getNotice(Observer<NoticeBean> observer) {
        Observable<NoticeBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getNotice();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getHomeInfo(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getHomeInfo();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void getOnLineUSer(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getHomeInfo();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }
}
