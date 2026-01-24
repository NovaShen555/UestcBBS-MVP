package com.novashen.riverside.module.report

import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.base.BasePresenter
import com.novashen.riverside.entity.ReportBean
import com.novashen.riverside.helper.ExceptionHelper.ResponseThrowable
import com.novashen.riverside.helper.rxhelper.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by sca_tl on 2022/12/16 14:56
 */
class ReportPresenter: BasePresenter<ReportView>() {

    private val reportModel = ReportModel()

    fun report(idType: String, message: String, id: Int) {
        reportModel.report(idType, message, id,
            object : Observer<ReportBean>() {
                override fun OnSuccess(reportBean: ReportBean) {
                    if (reportBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        view.onReportSuccess(reportBean)
                    }
                    if (reportBean.rs == ApiConstant.Code.ERROR_CODE) {
                        view.onReportError(reportBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    view.onReportError(e.message)
                }

                override fun OnCompleted() { }

                override fun OnDisposable(d: Disposable) {
                    disposable.add(d)
                }
            })
    }

}