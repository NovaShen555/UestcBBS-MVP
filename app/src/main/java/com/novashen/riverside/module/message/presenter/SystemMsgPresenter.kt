package com.novashen.riverside.module.message.presenter

import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.SystemMsgBean
import com.novashen.riverside.http.Observer
import com.novashen.riverside.module.message.model.MessageModel
import com.novashen.riverside.module.message.view.SystemMsgView
import com.novashen.riverside.util.subscribeEx

/**
 * Created by sca_tl at 2023/2/20 10:23
 */
class SystemMsgPresenter: BaseVBPresenter<SystemMsgView>() {

    private val messageModel = MessageModel()

    fun getSystemMsg(page: Int, pageSize: Int) {
        messageModel
            .getSystemMsg(page, pageSize)
            .subscribeEx(Observer<SystemMsgBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetSystemMsgSuccess(it)
                    }
                    if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetSystemMsgError(it.head.errInfo)
                    }
                }

                onError {
                    mView?.onGetSystemMsgError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }
}