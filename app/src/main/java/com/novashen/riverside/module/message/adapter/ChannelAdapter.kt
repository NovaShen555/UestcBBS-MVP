package com.novashen.riverside.module.message.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.novashen.riverside.R
import com.novashen.riverside.api.discourse.entity.ChatChannel
import com.novashen.util.ColorUtil

class ChannelAdapter(layoutResId: Int) : BaseQuickAdapter<ChatChannel, BaseViewHolder>(layoutResId) {

    private fun parseIsoTime(isoTime: String?): String {
        if (isoTime.isNullOrEmpty()) return ""
        return try {
            val pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
            val sdf = java.text.SimpleDateFormat(pattern, java.util.Locale.US)
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = sdf.parse(isoTime)
            if (date != null) {
                val time = date.time
                val d = System.currentTimeMillis() - time
                if (d < 60000) return "刚刚"
                if (d < 3600000) return "${d/60000}分钟前"
                if (d < 86400000) return "${d/3600000}小时前"
                val yearPattern = if (com.novashen.riverside.util.TimeUtil.isCurrentYear(time)) "MM-dd HH:mm" else "yyyy-MM-dd HH:mm"
                java.text.SimpleDateFormat(yearPattern, java.util.Locale.CHINA).format(date)
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    override fun convert(helper: BaseViewHolder, item: ChatChannel) {
        helper.setText(R.id.tv_channel_title, item.title)

        val avatarText = helper.getView<TextView>(R.id.tv_channel_avatar)
        val initial = item.title?.trim()?.firstOrNull()?.toString() ?: "#"
        avatarText.text = initial

        val colorHex = item.chatable?.color
        val avatarColor = try {
            if (!colorHex.isNullOrEmpty()) Color.parseColor("#${colorHex.trim().removePrefix("#")}")
            else ColorUtil.getAttrColor(mContext, R.attr.colorPrimary)
        } catch (e: Exception) {
            ColorUtil.getAttrColor(mContext, R.attr.colorPrimary)
        }
        avatarText.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(avatarColor)
        }

        val subtitle = when {
            !item.chatable?.description.isNullOrEmpty() -> item.chatable?.description
            !item.chatable?.name.isNullOrEmpty() -> item.chatable?.name
            else -> ""
        }
        helper.setText(R.id.tv_channel_subtitle, subtitle)
        
        val lastMsg = item.lastMessage
        if (lastMsg != null) {
            val content = lastMsg.excerpt ?: lastMsg.message ?: ""
            helper.setText(R.id.tv_last_message, content)
            helper.setText(R.id.tv_channel_time, parseIsoTime(lastMsg.createdAt))
        } else {
            helper.setText(R.id.tv_last_message, "暂无消息")
            helper.setText(R.id.tv_channel_time, "")
        }
    }
}
