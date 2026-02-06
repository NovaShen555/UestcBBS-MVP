package com.novashen.riverside.module.message.presenter

import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil
import com.novashen.riverside.api.discourse.entity.ChatMessagesResponse
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.helper.ExceptionHelper
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.module.message.view.ChannelChatView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class ChannelChatPresenter : BaseVBPresenter<ChannelChatView>() {

    fun getMessages(channelId: Int) {
        DiscourseRetrofitUtil.getInstance()
            .apiService
            .getChatMessages(channelId, 50, true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ChatMessagesResponse>() {
                override fun OnSuccess(response: ChatMessagesResponse?) {
                    if (response != null && response.messages != null) {
                        mView?.onGetMessagesSuccess(response.messages)
                        if (response.messages.isNotEmpty()) {
                            val lastId = response.messages.last().id
                            markRead(channelId, lastId)
                        }
                    } else {
                        mView?.onGetMessagesError("消息为空")
                    }
                }

                override fun onError(e: ExceptionHelper.ResponseThrowable?) {
                    mView?.onGetMessagesError("获取消息失败：" + (e?.message ?: ""))
                }
                
                override fun OnCompleted() {}
                override fun OnDisposable(d: Disposable?) {
                    if (d != null) {
                        mCompositeDisposable?.add(d)
                    }
                }
            })
    }

    fun getMessagesWithPageSize(channelId: Int, pageSize: Int) {
        DiscourseRetrofitUtil.getInstance()
            .apiService
            .getChatMessagesLatest(channelId, pageSize)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ChatMessagesResponse>() {
                override fun OnSuccess(response: ChatMessagesResponse?) {
                    if (response != null && response.messages != null) {
                        mView?.onGetMessagesSuccess(response.messages)
                        if (response.messages.isNotEmpty()) {
                            val lastId = response.messages.last().id
                            markRead(channelId, lastId)
                        }
                    } else {
                        mView?.onGetMessagesError("消息为空")
                    }
                }

                override fun onError(e: ExceptionHelper.ResponseThrowable?) {
                    mView?.onGetMessagesError("获取消息失败：" + (e?.message ?: ""))
                }

                override fun OnCompleted() {}
                override fun OnDisposable(d: Disposable?) {
                    if (d != null) {
                        mCompositeDisposable?.add(d)
                    }
                }
            })
    }

    fun getMessagesHistory(channelId: Int, direction: String, targetMessageId: Int) {
        DiscourseRetrofitUtil.getInstance()
            .apiService
            .getChatMessagesHistory(channelId, 50, direction, targetMessageId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ChatMessagesResponse>() {
                override fun OnSuccess(response: ChatMessagesResponse?) {
                    if (response != null && response.messages != null) {
                        mView?.onGetHistorySuccess(response.messages)
                    } else {
                        mView?.onGetHistoryError("消息为空")
                    }
                }

                override fun onError(e: ExceptionHelper.ResponseThrowable?) {
                    mView?.onGetHistoryError("获取历史消息失败：" + (e?.message ?: ""))
                }

                override fun OnCompleted() {}
                override fun OnDisposable(d: Disposable?) {
                    if (d != null) {
                        mCompositeDisposable?.add(d)
                    }
                }
            })
    }
    
    fun markRead(channelId: Int, messageId: Int) {
         DiscourseRetrofitUtil.getInstance()
            .apiService
            .markChatRead(channelId, messageId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<ResponseBody> {
                 override fun onSubscribe(d: Disposable) { mCompositeDisposable?.add(d) }
                 override fun onNext(t: ResponseBody) {}
                 override fun onError(e: Throwable) {}
                 override fun onComplete() {}
            })
    }

    fun getLatestMessageId(channelId: Int) {
        DiscourseRetrofitUtil.getInstance()
            .apiService
            .getChatMessagesLatest(channelId, 1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ChatMessagesResponse>() {
                override fun OnSuccess(response: ChatMessagesResponse?) {
                    val latestId = response?.messages?.maxOfOrNull { it.id } ?: 0
                    if (latestId > 0) {
                        mView?.onGetLatestIdSuccess(latestId)
                    } else {
                        mView?.onGetLatestIdError("最新消息为空")
                    }
                }

                override fun onError(e: ExceptionHelper.ResponseThrowable?) {
                    mView?.onGetLatestIdError("获取最新消息失败：" + (e?.message ?: ""))
                }

                override fun OnCompleted() {}
                override fun OnDisposable(d: Disposable?) {
                    if (d != null) {
                        mCompositeDisposable?.add(d)
                    }
                }
            })
    }

    fun sendMessage(channelId: Int, message: String) {
        val convertedMessage = convertEmojiFormat(message)
        val stagedId = UUID.randomUUID().toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val clientCreatedAt = dateFormat.format(System.currentTimeMillis())

        DiscourseRetrofitUtil.getInstance()
            .apiService
            .sendChatMessage(channelId, convertedMessage, stagedId, clientCreatedAt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }

                override fun onNext(t: ResponseBody) {
                    mView?.onSendMessageSuccess()
                }

                override fun onError(e: Throwable) {
                    mView?.onSendMessageError(e.message ?: "发送失败")
                }

                override fun onComplete() {}
            })
    }

    private fun convertEmojiFormat(text: String): String {
        return text.replace(Regex("\\[([a-zA-Z]+):(\\d+)]")) { matchResult ->
            val letter = matchResult.groupValues[1]
            val number = matchResult.groupValues[2]
            val convertedLetter = if (letter == "a") "s" else letter
            ":$convertedLetter$number:"
        }
    }
}
