package com.novashen.riverside.module.message.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.novashen.riverside.R
import com.novashen.riverside.annotation.ToastType
import com.novashen.riverside.api.discourse.entity.ChatMessage
import com.novashen.riverside.base.BaseVBActivity
import com.novashen.riverside.databinding.ActivityChannelChatBinding
import com.novashen.riverside.module.message.adapter.ChannelChatAdapter
import com.novashen.riverside.module.message.presenter.ChannelChatPresenter
import com.novashen.riverside.util.showToast
import com.novashen.widget.emotion.IEmotionEventListener

class ChannelChatActivity : BaseVBActivity<ChannelChatPresenter, ChannelChatView, ActivityChannelChatBinding>(), ChannelChatView,
    IEmotionEventListener {

    private lateinit var adapter: ChannelChatAdapter
    private var channelId: Int = 0
    private var channelTitle: String = ""
    private var isLoadingMore = false
    private var lastDirection = "past"
    private var lastLoadFromRefresh = false
    private var latestKnownId: Int = 0

    companion object {
        private const val KEY_CHANNEL_ID = "channel_id"
        private const val KEY_CHANNEL_TITLE = "channel_title"

        fun start(context: Context, channelId: Int, title: String) {
             val intent = Intent(context, ChannelChatActivity::class.java)
             intent.putExtra(KEY_CHANNEL_ID, channelId)
             intent.putExtra(KEY_CHANNEL_TITLE, title)
             context.startActivity(intent)
        }
    }

    override fun getViewBinding() = ActivityChannelChatBinding.inflate(layoutInflater)

    override fun initPresenter() = ChannelChatPresenter()

    override fun getContext(): Context? = this

    override fun initView(theftProof: Boolean) {
        super.initView(theftProof)
        channelId = intent.getIntExtra(KEY_CHANNEL_ID, 0)
        channelTitle = intent.getStringExtra(KEY_CHANNEL_TITLE) ?: ""
        
        updateToolbarTitle()
        mBinding.toolbar.setNavigationOnClickListener { finish() }
        
        adapter = ChannelChatAdapter(R.layout.item_chat_channel_msg)
        mBinding.chatRecycler.layoutManager = LinearLayoutManager(this).apply {
             stackFromEnd = true
        }
        mBinding.chatRecycler.adapter = adapter
        mBinding.chatRecycler.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                updateToolbarTitle()
                updateJumpLatestButton()
            }
        })

        bindClickEvent(mBinding.addPhotoBtn, mBinding.addEmotionBtn, mBinding.sendMsgBtn, mBinding.edittext)
        mBinding.emotionLayout.eventListener = this
        
        mBinding.refreshLayout.setOnRefreshListener {
             loadPast()
        }

        mBinding.refreshLayout.setOnLoadMoreListener {
            loadFuture()
        }

        mBinding.btnJumpLatest.setOnClickListener {
            mPresenter?.getMessagesWithPageSize(channelId, 100)
        }
        
        mPresenter?.getMessages(channelId)
        mPresenter?.getLatestMessageId(channelId)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            mBinding.addPhotoBtn -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                showToast("暂不支持图片发送", ToastType.TYPE_NORMAL)
            }
            mBinding.addEmotionBtn -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                if (mBinding.emotionLayout.visibility == View.GONE) {
                    mBinding.smoothInputLayout.showInputPane(true)
                    mBinding.emotionLayout.visibility = View.VISIBLE
                } else {
                    mBinding.smoothInputLayout.showKeyboard()
                }
            }
            mBinding.edittext -> {
                mBinding.smoothInputLayout.showKeyboard()
            }
            mBinding.sendMsgBtn -> {
                val content = mBinding.edittext.text.toString().trim()
                if (content.isNotEmpty()) {
                    mPresenter?.sendMessage(channelId, content)
                }
            }
        }
    }

    override fun onEmotionClick(path: String?) {
        if (path == null) return
        val index = path.lastIndexOf("/")
        val name = if (index != -1) path.substring(index + 1) else path
        val regex = Regex("\\[([as])_(\\d+)]")
        val match = regex.find(name)
        if (match != null) {
            val letter = match.groupValues[1]
            val number = match.groupValues[2]
            mBinding.edittext.append("[$letter:$number]")
        }
    }

    private fun loadPast() {
        if (isLoadingMore) {
            mBinding.refreshLayout.finishRefresh()
            return
        }
        val firstId = adapter.data.minByOrNull { it.id }?.id ?: run {
            mBinding.refreshLayout.finishRefresh()
            return
        }
        isLoadingMore = true
        lastDirection = "past"
        lastLoadFromRefresh = true
        mPresenter?.getMessagesHistory(channelId, "past", firstId)
    }

    private fun loadFuture() {
        if (isLoadingMore) {
            mBinding.refreshLayout.finishLoadMore()
            return
        }
        val lastId = adapter.data.maxByOrNull { it.id }?.id ?: run {
            mBinding.refreshLayout.finishLoadMore()
            return
        }
        isLoadingMore = true
        lastDirection = "future"
        lastLoadFromRefresh = false
        mPresenter?.getMessagesHistory(channelId, "future", lastId)
    }

    override fun onGetMessagesSuccess(messages: List<ChatMessage>) {
        mBinding.refreshLayout.finishRefresh()
        adapter.setNewData(messages)
        if (messages.isNotEmpty()) {
             mBinding.chatRecycler.scrollToPosition(messages.size - 1)
        }
        updateToolbarTitle()
        updateJumpLatestButton()
        mPresenter?.getLatestMessageId(channelId)
    }

    override fun onGetMessagesError(msg: String) {
        mBinding.refreshLayout.finishRefresh()
        showToast(msg, type = ToastType.TYPE_ERROR)
    }

    override fun onGetHistorySuccess(messages: List<ChatMessage>) {
        isLoadingMore = false
        val existingIds = adapter.data.map { it.id }.toHashSet()
        val newMessages = messages.filterNot { existingIds.contains(it.id) }
        if (newMessages.isEmpty()) {
            if (lastLoadFromRefresh) {
                mBinding.refreshLayout.finishRefresh()
            } else {
                mBinding.refreshLayout.finishLoadMore()
            }
            return
        }

        val ordered = newMessages.sortedBy { it.id }
        val layoutManager = mBinding.chatRecycler.layoutManager as? LinearLayoutManager

        if (lastDirection == "past") {
            val firstVisible = layoutManager?.findFirstVisibleItemPosition() ?: 0
            val firstView = mBinding.chatRecycler.getChildAt(0)
            val top = firstView?.top ?: 0
            adapter.addData(0, ordered)
            layoutManager?.scrollToPositionWithOffset(firstVisible + ordered.size, top)
        } else {
            val lastVisible = layoutManager?.findLastVisibleItemPosition() ?: -1
            val shouldAutoScroll = lastVisible >= adapter.data.size - 2
            adapter.addData(ordered)
            if (shouldAutoScroll) {
                mBinding.chatRecycler.scrollToPosition(adapter.data.size - 1)
            }
        }

        if (lastLoadFromRefresh) {
            mBinding.refreshLayout.finishRefresh()
        } else {
            mBinding.refreshLayout.finishLoadMore()
        }

        if (lastDirection == "future") {
            latestKnownId = maxOf(latestKnownId, ordered.maxOfOrNull { it.id } ?: latestKnownId)
        }
        updateToolbarTitle()
        updateJumpLatestButton()
    }

    override fun onGetHistoryError(msg: String) {
        isLoadingMore = false
        if (lastLoadFromRefresh) {
            mBinding.refreshLayout.finishRefresh()
        } else {
            mBinding.refreshLayout.finishLoadMore()
        }
        showToast(msg, type = ToastType.TYPE_ERROR)
    }

    override fun onGetLatestIdSuccess(latestId: Int) {
        latestKnownId = latestId
        updateJumpLatestButton()
    }

    override fun onGetLatestIdError(msg: String) {
        // ignore
    }

    override fun onSendMessageSuccess() {
        mBinding.edittext.setText("")
        mBinding.smoothInputLayout.showKeyboard()
        mPresenter?.getMessages(channelId)
    }

    override fun onSendMessageError(msg: String) {
        showToast(msg, type = ToastType.TYPE_ERROR)
    }

    private fun getVisibleMaxId(): Int {
        val layoutManager = mBinding.chatRecycler.layoutManager as? LinearLayoutManager ?: return 0
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()
        if (first == androidx.recyclerview.widget.RecyclerView.NO_POSITION || last == androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
            return 0
        }
        var maxId = 0
        for (i in first..last) {
            val item = adapter.data.getOrNull(i) ?: continue
            if (item.id > maxId) maxId = item.id
        }
        return maxId
    }

    private fun updateToolbarTitle() {
        val visibleMax = getVisibleMaxId()
        val suffix = if (visibleMax > 0) " (#$visibleMax)" else ""
        mBinding.toolbar.title = channelTitle + suffix
    }

    private fun updateJumpLatestButton() {
        val currentMax = getVisibleMaxId()
        val diff = latestKnownId - currentMax
        mBinding.btnJumpLatest.visibility = if (diff > 50) View.VISIBLE else View.GONE
    }
}
