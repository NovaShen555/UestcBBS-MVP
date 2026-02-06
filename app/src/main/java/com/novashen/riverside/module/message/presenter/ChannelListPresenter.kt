package com.novashen.riverside.module.message.presenter

import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil
import com.novashen.riverside.api.discourse.entity.ChatChannelsResponse
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.helper.ExceptionHelper
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.module.message.view.ChannelListView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChannelListPresenter : BaseVBPresenter<ChannelListView>() {

    fun getChannels() {
        DiscourseRetrofitUtil.getInstance()
            .apiService
            .getChatChannels(10, "", "all")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ChatChannelsResponse>() {
                override fun OnSuccess(response: ChatChannelsResponse?) {
                    if (response != null && response.channels != null) {
                        mView?.onGetChannelsSuccess(response.channels)
                    } else {
                        mView?.onGetChannelsError("获取频道失败：响应为空")
                    }
                }

                override fun onError(e: ExceptionHelper.ResponseThrowable?) {
                    mView?.onGetChannelsError("获取频道失败：" + (e?.message ?: ""))
                }

                override fun OnCompleted() {
                }

                override fun OnDisposable(d: Disposable?) {
                    if (d != null) {
                        mCompositeDisposable?.add(d)
                    }
                }
            })
    }
}
