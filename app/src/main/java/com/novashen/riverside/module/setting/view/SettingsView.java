package com.novashen.riverside.module.setting.view;

import com.novashen.riverside.entity.UpdateBean;

/**
 * author: sca_tl
 * description:
 * date: 2019/12/15 16:54
 */
public interface SettingsView {
    void getUpdateSuccess(UpdateBean updateBean);
    void getUpdateFail(String msg);
    void getCacheSizeSuccess(String msg);
    void getCacheSizeFail(String msg);
}
