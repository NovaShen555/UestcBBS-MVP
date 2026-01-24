package com.novashen.riverside.module.report

import com.novashen.riverside.entity.ReportBean
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.util.RetrofitUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by sca_tl on 2022/12/16 15:55
 */
class ReportModel {
    fun report(idType: String, message: String, id: Int, observer: Observer<ReportBean>) {
        val observable = RetrofitUtil
            .getInstance()
            .apiService
            .report(idType, message, id)
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }
}