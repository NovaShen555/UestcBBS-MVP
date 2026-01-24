package com.novashen.riverside.module.message.presenter

import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.ReplyMeMsgBean
import com.novashen.riverside.http.Observer
import com.novashen.riverside.module.message.model.MessageModel
import com.novashen.riverside.module.message.view.ReplyMeMsgView
import com.novashen.riverside.util.subscribeEx

/**
 * Created by sca_tl at 2023/2/17 10:05
 */
class ReplyMeMsgPresenter: BaseVBPresenter<ReplyMeMsgView>() {

    private val messageModel = MessageModel()

    fun getReplyMeMsg(page: Int, pageSize: Int) {
        messageModel
            .getReplyMsg(page, pageSize)
            .subscribeEx(Observer<ReplyMeMsgBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetReplyMeMsgSuccess(it)
                    }
                    if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetReplyMeMsgError(it.head.errInfo)
                    }
                }

                onError {
                    mView?.onGetReplyMeMsgError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }
}