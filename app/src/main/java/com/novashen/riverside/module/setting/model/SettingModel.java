package com.novashen.riverside.module.setting.model;

import android.content.Context;

import com.novashen.riverside.entity.UpdateBean;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.util.Constant;
import com.novashen.riverside.util.RetrofitUtil;
import com.novashen.util.FileUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 18:13
 */
public class SettingModel {
    public void getUpdate(int oldVersionCode, boolean isTest, Observer<UpdateBean> observer) {
        Observable<UpdateBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getUpdateInfo(oldVersionCode, isTest);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getCacheSize(Context context, Observer<String> observer) {
        Observable
                .create((ObservableOnSubscribe<String>) emitter -> {
                    String s = FileUtil.formatFileSize(FileUtil.getDirectorySize(context.getCacheDir())
                            + FileUtil.getDirectorySize(context.getExternalFilesDir(Constant.AppPath.TEMP_PATH)));
                    emitter.onNext(s);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
