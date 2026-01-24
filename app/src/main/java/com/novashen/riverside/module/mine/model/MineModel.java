package com.novashen.riverside.module.mine.model;

import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.util.RetrofitCookieUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 12:51
 */
public class MineModel {
    public void userGroup(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .userGroup();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
