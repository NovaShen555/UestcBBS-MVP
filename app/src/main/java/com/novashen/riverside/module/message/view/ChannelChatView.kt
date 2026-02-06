package com.novashen.riverside.module.message.view

import com.novashen.riverside.api.discourse.entity.ChatMessage
import com.novashen.riverside.base.BaseView

interface ChannelChatView : BaseView {
    fun onGetMessagesSuccess(messages: List<ChatMessage>)
    fun onGetMessagesError(msg: String)
    fun onGetHistorySuccess(messages: List<ChatMessage>)
    fun onGetHistoryError(msg: String)
    fun onGetLatestIdSuccess(latestId: Int)
    fun onGetLatestIdError(msg: String)
    fun onSendMessageSuccess()
    fun onSendMessageError(msg: String)
}
