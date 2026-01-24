package com.novashen.riverside.module.setting.presenter;

import android.content.Context;

import com.novashen.riverside.R;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.entity.UpdateBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.setting.model.SettingModel;
import com.novashen.riverside.module.setting.view.SettingsView;

import io.reactivex.disposables.Disposable;


/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 13:20
 */
public class SettingsPresenter extends BasePresenter<SettingsView> {

    private SettingModel settingModel = new SettingModel();

    public void getUpdate(int oldVersionCode, boolean isTest) {
        settingModel.getUpdate(oldVersionCode, isTest, new Observer<UpdateBean>() {
            @Override
            public void OnSuccess(UpdateBean updateBean) {
                view.getUpdateSuccess(updateBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getUpdateFail(e.message);
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

    public void getCacheSize(Context context) {
        settingModel.getCacheSize(context, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                view.getCacheSizeSuccess(s);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getCacheSizeFail(e.message);
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
