package com.novashen.riverside.module.message.presenter

import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.DianPingMsgBean
import com.novashen.riverside.http.Observer
import com.novashen.riverside.module.message.model.MessageModel
import com.novashen.riverside.module.message.view.DianPingMsgView
import com.novashen.riverside.util.subscribeEx

/**
 * Created by sca_tl at 2023/2/17 10:29
 */
class DianPingMsgPresenter: BaseVBPresenter<DianPingMsgView>() {
    private val messageModel = MessageModel()

    fun getDianPingMsg(page: Int, pageSize: Int) {
        messageModel
            .getDianPingMsg(page, pageSize)
            .subscribeEx(Observer<DianPingMsgBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetDianPingMsgSuccess(it)
                    } else if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetDianPingMsgError(it.head?.errInfo)
                    }
                }

                onError {
                    mView?.onGetDianPingMsgError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }
}