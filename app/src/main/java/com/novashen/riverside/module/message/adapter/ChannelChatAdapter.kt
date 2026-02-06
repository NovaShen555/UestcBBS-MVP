package com.novashen.riverside.module.message.adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.app.Dialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.novashen.riverside.R
import com.novashen.riverside.api.discourse.entity.ChatMessage
import com.novashen.riverside.api.discourse.converter.DiscourseDataConverter
import com.novashen.riverside.manager.EmotionManager
import com.novashen.riverside.module.post.adapter.HtmlImageGetter
import com.novashen.riverside.module.post.adapter.VerticalImageSpan
import com.novashen.riverside.util.SharePrefUtil
import com.novashen.riverside.util.TimeUtil
import com.novashen.riverside.widget.textview.EmojiTextView
import com.novashen.util.ColorUtil
import com.novashen.util.ScreenUtil

class ChannelChatAdapter(layoutResId: Int) : BaseQuickAdapter<ChatMessage, BaseViewHolder>(layoutResId) {

    private fun parseIsoTime(isoTime: String?): String {
        if (isoTime.isNullOrEmpty()) return ""
        try {
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
                 val yearPattern = if (TimeUtil.isCurrentYear(time)) "MM-dd HH:mm" else "yyyy-MM-dd HH:mm"
                 return java.text.SimpleDateFormat(yearPattern, java.util.Locale.CHINA).format(date)
             }
        } catch (e: Exception) {
        }
        return isoTime ?: ""
    }

    override fun convert(helper: BaseViewHolder, item: ChatMessage) {
        val myUid = SharePrefUtil.getUid(mContext)
        val senderId = item.user?.id ?: -1

        if (senderId == myUid) {
            helper.getView<View>(R.id.his_content_layout).visibility = View.GONE
            helper.getView<View>(R.id.mine_content_layout).visibility = View.VISIBLE
            setMineContent(helper, item)
        } else {
            helper.getView<View>(R.id.mine_content_layout).visibility = View.GONE
            helper.getView<View>(R.id.his_content_layout).visibility = View.VISIBLE
            setHisContent(helper, item)
        }
    }

    private fun setMineContent(helper: BaseViewHolder, item: ChatMessage) {
        val icon = helper.getView<ImageView>(R.id.mine_icon)
        val nameView = helper.getView<TextView>(R.id.mine_username)
        val bg = helper.getView<View>(R.id.mine_content_bg)
        val textContent = helper.getView<EmojiTextView>(R.id.mine_text_content)
        val imgContent = helper.getView<ShapeableImageView>(R.id.mine_img_content)
        val time = helper.getView<TextView>(R.id.mine_time)

        val avatar = DiscourseDataConverter.getAvatarUrl(item.user?.avatarTemplate, 100)
        Glide.with(mContext)
            .load(avatar)
            .placeholder(R.drawable.ic_anonymous)
            .error(R.drawable.ic_anonymous)
            .into(icon)
        val name = item.user?.username ?: ""
        if (name.isBlank()) {
            nameView.visibility = View.GONE
        } else {
            nameView.visibility = View.VISIBLE
            nameView.text = name
        }
        time.text = parseIsoTime(item.createdAt)

        val cornerR = ScreenUtil.dip2pxF(mContext, 15f)
        bg.background = GradientDrawable().apply {
            setColor(ColorUtil.getAttrColor(mContext, R.attr.colorPrimaryContainer))
            cornerRadii = floatArrayOf(cornerR, cornerR, 0f, 0f, cornerR, cornerR, cornerR, cornerR)
        }

        textContent.visibility = View.VISIBLE
        imgContent.visibility = View.GONE
        renderMessage(textContent, item.message, item.cooked)
        renderUpload(textContent, imgContent, item)
        time.text = "#${item.id} · ${time.text}"
        bg.backgroundTintList = ColorStateList.valueOf(ColorUtil.getAttrColor(mContext, R.attr.colorPrimaryContainer))
    }

    private fun setHisContent(helper: BaseViewHolder, item: ChatMessage) {
        val icon = helper.getView<ImageView>(R.id.his_icon)
        val nameView = helper.getView<TextView>(R.id.his_username)
        val bg = helper.getView<View>(R.id.his_content_bg)
        val textContent = helper.getView<EmojiTextView>(R.id.his_text_content)
        val imgContent = helper.getView<ShapeableImageView>(R.id.his_img_content)
        val time = helper.getView<TextView>(R.id.his_time)

        val avatar = DiscourseDataConverter.getAvatarUrl(item.user?.avatarTemplate, 100)
        Glide.with(mContext)
            .load(avatar)
            .placeholder(R.drawable.ic_anonymous)
            .error(R.drawable.ic_anonymous)
            .into(icon)
        val name = item.user?.username ?: ""
        if (name.isBlank()) {
            nameView.visibility = View.GONE
        } else {
            nameView.visibility = View.VISIBLE
            nameView.text = name
        }
        time.text = parseIsoTime(item.createdAt)

        val cornerR = ScreenUtil.dip2pxF(mContext, 15f)
        bg.background = GradientDrawable().apply {
            setColor(ColorUtil.getAttrColor(mContext, R.attr.colorSurface))
            cornerRadii = floatArrayOf(0f, 0f, cornerR, cornerR, cornerR, cornerR, cornerR, cornerR)
        }

        textContent.visibility = View.VISIBLE
        imgContent.visibility = View.GONE
        renderMessage(textContent, item.message, item.cooked)
        renderUpload(textContent, imgContent, item)
        time.text = "#${item.id} · ${time.text}"
    }

    private fun renderMessage(textView: EmojiTextView, message: String?, cooked: String?) {
        var textData = if (!cooked.isNullOrEmpty()) cooked else (message ?: "")
        if (textData.isEmpty()) {
            textView.text = ""
            textView.visibility = View.GONE
            return
        }

        textView.visibility = View.VISIBLE

        textData = trimTrailingBreaks(textData)
        textData = normalizeCooked(textData)

        if (cooked.isNullOrEmpty()) {
            textData = convertEmotion(textData)
        }

        if (textData.contains("<img") || textData.contains("<p>") || textData.contains("<br>")) {
            val spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(textData, Html.FROM_HTML_MODE_LEGACY, HtmlImageGetter(textView, mContext), null)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(textData, HtmlImageGetter(textView, mContext), null)
            }

            val spannable = SpannableStringBuilder(spanned)
            val imageSpans = spannable.getSpans(0, spannable.length, ImageSpan::class.java)
            for (span in imageSpans) {
                val start = spannable.getSpanStart(span)
                val end = spannable.getSpanEnd(span)
                val flags = spannable.getSpanFlags(span)
                spannable.removeSpan(span)
                spannable.setSpan(VerticalImageSpan(span.drawable), start, end, flags)
            }
            textView.text = spannable
        } else {
            textData = trimTrailingBreaks(textData.replace("\r\n", "<br>"))
            textView.setText(textData)
        }
    }

    private fun trimTrailingBreaks(text: String): String {
        var result = text.trimEnd()
        val patterns = listOf("<br>", "<br/>", "<br />")
        var changed: Boolean
        do {
            changed = false
            result = result.replace(Regex("(?i)(<p>(?:\\s|&nbsp;|<br\\s*/?>)*</p>)+$"), "").trimEnd()
            for (p in patterns) {
                if (result.endsWith(p)) {
                    result = result.dropLast(p.length).trimEnd()
                    changed = true
                }
            }
            if (result.endsWith("\n") || result.endsWith("\r")) {
                result = result.trimEnd('\n', '\r')
                changed = true
            }
        } while (changed)
        return result
    }

    private fun normalizeCooked(text: String): String {
        var result = text
        result = result.replace(Regex("(?i)</p>\\s*<p>"), "<br>")
        result = result.replace(Regex("(?i)^<p>(.*)</p>$", RegexOption.DOT_MATCHES_ALL), "$1")
        result = result.replace("src=\"/", "src=\"https://river-side.cc/")
        result = result.replace("data-large-src=\"/", "data-large-src=\"https://river-side.cc/")
        result = result.replace("data-download-href=\"/", "data-download-href=\"https://river-side.cc/")
        return result
    }

    private fun convertEmotion(text: String): String {
        if (text.isEmpty()) return text
        val regex = Regex(":([as])(\\d+):")
        return regex.replace(text) { match ->
            val letter = match.groupValues[1]
            val number = match.groupValues[2]
            val name = "[${letter}_${number}]"
            val item = EmotionManager.INSTANCE.getEmotionByName(name)
            if (item != null) {
                "[mobcent_phiz=${item.aPath}]"
            } else {
                match.value
            }
        }
    }

    private fun renderUpload(
        textContent: EmojiTextView,
        imgContent: ShapeableImageView,
        item: ChatMessage
    ) {
        val upload = item.uploads?.firstOrNull()
        if (upload == null) {
            imgContent.visibility = View.GONE
            textContent.setOnClickListener(null)
            return
        }

        val uploadUrl = normalizeUploadUrl(upload.url)
        val thumbnailUrl = normalizeUploadUrl(upload.thumbnail?.url)

        if (isImageUpload(upload, uploadUrl, thumbnailUrl)) {
            imgContent.visibility = View.VISIBLE
            Glide.with(mContext)
                .load(thumbnailUrl ?: uploadUrl)
                .placeholder(R.drawable.ic_photo)
                .error(R.drawable.ic_photo)
                .into(imgContent)
            imgContent.setOnClickListener {
                showImagePreview(uploadUrl)
            }
            textContent.setOnClickListener(null)
        } else {
            imgContent.visibility = View.GONE
            val name = upload.originalFilename ?: uploadUrl
            if (textContent.visibility != View.VISIBLE) {
                textContent.visibility = View.VISIBLE
            }
            if (!textContent.text.isNullOrEmpty()) {
                textContent.append("\n")
            }
            textContent.append("附件: $name")
            textContent.setOnClickListener {
                openUrl(uploadUrl)
            }
        }
    }

    private fun isImageUpload(
        upload: ChatMessage.Upload,
        uploadUrl: String?,
        thumbnailUrl: String?
    ): Boolean {
        val ext = upload.extension?.lowercase().orEmpty()
        if (ext in setOf("jpg", "jpeg", "png", "gif", "webp", "bmp")) return true
        val url = uploadUrl?.lowercase().orEmpty()
        val thumb = thumbnailUrl?.lowercase().orEmpty()
        return url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") ||
            url.endsWith(".gif") || url.endsWith(".webp") || url.endsWith(".bmp") ||
            thumb.endsWith(".jpg") || thumb.endsWith(".jpeg") || thumb.endsWith(".png") ||
            thumb.endsWith(".gif") || thumb.endsWith(".webp") || thumb.endsWith(".bmp")
    }

    private fun normalizeUploadUrl(url: String?): String? {
        if (url.isNullOrEmpty()) return null
        return if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "https://river-side.cc$url"
        }
    }

    private fun showImagePreview(url: String?) {
        if (url.isNullOrBlank()) return
        try {
            val dialog = Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            val container = FrameLayout(mContext)
            container.setBackgroundColor(Color.parseColor("#CC000000"))
            val imageView = ImageView(mContext)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            val padding = ScreenUtil.dip2px(mContext, 16f)
            imageView.setPadding(padding, padding, padding, padding)
            container.addView(
                imageView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            container.setOnClickListener { dialog.dismiss() }
            dialog.setContentView(container)
            dialog.setCancelable(true)
            Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.ic_photo)
                .error(R.drawable.ic_photo)
                .into(imageView)
            dialog.show()
        } catch (e: Exception) {
        }
    }

    private fun openUrl(url: String?) {
        if (url.isNullOrBlank()) return
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext.startActivity(intent)
        } catch (e: Exception) {
        }
    }
}
