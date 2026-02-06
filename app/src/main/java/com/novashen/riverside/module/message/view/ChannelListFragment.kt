package com.novashen.riverside.module.message.view

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.novashen.riverside.R
import com.novashen.riverside.api.discourse.entity.ChatChannel
import com.novashen.riverside.base.BaseVBFragment
import com.novashen.riverside.databinding.FragmentChannelListBinding
import com.novashen.riverside.module.message.adapter.ChannelAdapter
import com.novashen.riverside.module.message.presenter.ChannelListPresenter
import com.novashen.riverside.util.showToast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.novashen.riverside.annotation.ToastType

class ChannelListFragment : BaseVBFragment<ChannelListPresenter, ChannelListView, FragmentChannelListBinding>(), ChannelListView {

    private lateinit var adapter: ChannelAdapter

    companion object {
        fun getInstance(bundle: Bundle?) = ChannelListFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentChannelListBinding.inflate(layoutInflater)

    override fun initPresenter() = ChannelListPresenter()

    override fun getContext(): android.content.Context? = super.getContext()

    override fun initView() {
        super.initView()
        adapter = ChannelAdapter(R.layout.item_channel)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        mBinding.recyclerView.adapter = adapter
        
        mBinding.refreshLayout.setOnRefreshListener {
            mPresenter?.getChannels()
        }
        
        adapter.setOnItemClickListener { adapter, view, position ->
             val channel = adapter.data[position] as ChatChannel
             context?.let { ChannelChatActivity.start(it, channel.getId(), channel.getTitle()) }
        }
        
        mBinding.statusView.loading()
        mPresenter?.getChannels()
    }

    override fun onGetChannelsSuccess(channels: List<ChatChannel>) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.statusView.success()
        adapter.setNewData(channels)
    }

    override fun onGetChannelsError(msg: String) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.statusView.error(msg)
        showToast(msg, ToastType.TYPE_ERROR)
    }
}
