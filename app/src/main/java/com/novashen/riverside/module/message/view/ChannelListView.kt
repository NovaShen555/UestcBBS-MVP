package com.novashen.riverside.module.message.view

import com.novashen.riverside.api.discourse.entity.ChatChannel
import com.novashen.riverside.base.BaseView

interface ChannelListView : BaseView {
    fun onGetChannelsSuccess(channels: List<ChatChannel>)
    fun onGetChannelsError(msg: String)
}
