package com.novashen.riverside.module.message.presenter

import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.AtMsgBean
import com.novashen.riverside.http.Observer
import com.novashen.riverside.module.message.model.MessageModel
import com.novashen.riverside.module.message.view.AtMeMsgView
import com.novashen.riverside.util.subscribeEx

/**
 * Created by sca_tl at 2023/2/17 14:06
 */
class AtMeMsgPresenter: BaseVBPresenter<AtMeMsgView>() {

    private val messageModel = MessageModel()

    fun getAtMeMsg(page: Int, pageSize: Int) {
        messageModel
            .getAtMeMsg(page, pageSize)
            .subscribeEx(Observer<AtMsgBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetAtMeMsgSuccess(it)
                    } else if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetAtMeMsgError(it.head.errInfo)
                    }
                }

                onError {
                    mView?.onGetAtMeMsgError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

}