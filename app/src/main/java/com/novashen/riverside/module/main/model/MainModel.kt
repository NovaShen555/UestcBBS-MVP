package com.novashen.riverside.module.main.model

import com.novashen.riverside.entity.SettingsBean
import com.novashen.riverside.entity.UpdateBean
import com.novashen.riverside.util.RetrofitUtil
import io.reactivex.Observable

/**
 * Created by sca_tl at 2023/4/11 17:20
 */
class MainModel {
    fun getUpdate(oldVersionCode: Int, isTest: Boolean): Observable<UpdateBean> =
        RetrofitUtil
            .getInstance()
            .apiService
            .getUpdateInfo(oldVersionCode, isTest)

    fun getSettings(): Observable<SettingsBean> =
         RetrofitUtil
            .getInstance()
            .apiService
            .settings

}