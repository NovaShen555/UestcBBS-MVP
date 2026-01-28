package com.novashen.riverside.module.post.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.VectorDrawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chad.library.adapter.base.BaseViewHolder
import com.novashen.riverside.R
import com.novashen.riverside.entity.CommonPostBean
import com.novashen.riverside.manager.BlackListManager
import com.novashen.riverside.helper.PreloadAdapter
import com.novashen.riverside.module.post.view.CommonPostFragment
import com.novashen.riverside.util.Constant
import com.novashen.riverside.util.TimeUtil
import com.novashen.riverside.util.isNullOrEmpty
import com.novashen.riverside.util.load
import com.novashen.widget.sapn.CenterImageSpan
import com.novashen.util.ColorUtil
import com.novashen.util.ScreenUtil
import com.novashen.widget.ninelayout.NineGridLayout


/**
 * Created by sca_tl at 2023/4/25 17:23
 */
@SuppressLint("SetTextI18n")
class CommonPostAdapter(layoutResId: Int, val type: String = "", onPreload: (() -> Unit)? = null) :
    PreloadAdapter<CommonPostBean.ListBean, BaseViewHolder>(layoutResId, onPreload) {

    fun addData(newData: List<CommonPostBean.ListBean>, reload: Boolean) {
        val filterData = newData.filter {
            (if (reload) true else !data.contains(it)) && !BlackListManager.INSTANCE.isBlacked(it.user_id)
        }
        if (reload) {
            setNewData(filterData)
        } else {
            addData(filterData)
        }
    }

    override fun convert(helper: BaseViewHolder, item: CommonPostBean.ListBean) {
        super.convert(helper, item)
        val hideContent = false
        val avatar = helper.getView<ImageView>(R.id.avatar)
        val userName = helper.getView<TextView>(R.id.user_name)
        val time = helper.getView<TextView>(R.id.time)
        val title = helper.getView<TextView>(R.id.title)
        val content = helper.getView<TextView>(R.id.content)
        val boardName = helper.getView<TextView>(R.id.board_name)
        val imageLayout = helper.getView<NineGridLayout>(R.id.image_layout)
        val supportCount = helper.getView<TextView>(R.id.support_count)
        val commentCount = helper.getView<TextView>(R.id.comment_count)
        val viewCount = helper.getView<TextView>(R.id.view_count)

            helper
                .addOnClickListener(R.id.avatar)
                .addOnClickListener(R.id.board_name)
                .addOnClickListener(R.id.content_layout) // Make content clickable
            
            helper.itemView.setOnClickListener {
                if (item.topic_id != 0) {
                     val intent = Intent(mContext, com.novashen.riverside.module.post.view.NewPostDetailActivity::class.java)
                     intent.putExtra(Constant.IntentKey.TOPIC_ID, item.topic_id)
                     mContext.startActivity(intent)
                } else if (item.topic_id == 0 && item.board_id == 0) {
                    // Fallback for old style logic if any
                    val intent = Intent(mContext, com.novashen.riverside.module.post.PostDetailActivity::class.java)
                    intent.putExtra(Constant.IntentKey.TOPIC_ID, item.topic_id)
                    intent.putExtra(Constant.IntentKey.TYPE, type)
                    mContext.startActivity(intent)
                } else {
                     val intent = Intent(mContext, com.novashen.riverside.module.post.PostDetailActivity::class.java)
                     intent.putExtra(Constant.IntentKey.TOPIC_ID, item.topic_id)
                     intent.putExtra(Constant.IntentKey.TYPE, type)
                     mContext.startActivity(intent)
                }
            }


        userName.text = item.user_nick_name
        boardName.text = item.board_name
        supportCount.text = " ${item.recommendAdd}"
        commentCount.text = " ${item.replies}"
        viewCount.text = " ${item.hits}"
        
        // Fix Discourse Time
        if (item.last_reply_date != null && item.last_reply_date.length > 10) {
             // likely ms time string
             try {
                time.text = TimeUtil.getFormatDate(item.last_reply_date.toLong(), "yyyy-MM-dd") 
             } catch (e: Exception) {
                 time.text = item.last_reply_date
             }
        } else {
             time.text = item.last_reply_date
        }

        if (item.user_id == 0 || "匿名" == item.user_nick_name) {
            avatar.load(R.drawable.ic_anonymous)
        } else {
            avatar.load(item.userAvatar)
        }

        content.apply {
            text = if (!item.reply_content.isNullOrEmpty()) item.reply_content else item.subject
            visibility = if ((item.subject.isNullOrEmpty() && item.reply_content.isNullOrEmpty()) || hideContent == true) View.GONE else View.VISIBLE
        }


        if (item.vote == 1) {
            val spannableStringBuilder = SpannableStringBuilder("I" + item.title)
            val drawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_vote)
            if (drawable is VectorDrawable) {
                drawable.setTint(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary))
                val radio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
                val rect = Rect(0, 0, (title.textSize * radio * 1.1f).toInt(), (title.textSize * 1.1f).toInt())
                drawable.bounds = rect
                val imageSpan = CenterImageSpan(drawable).apply {
                    rightPadding = ScreenUtil.dip2px(mContext, 2f)
                }
                spannableStringBuilder.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
            title.text = spannableStringBuilder
        } else {
            title.text = item.title
        }

        if (type == CommonPostFragment.TYPE_HOT_POST) {
            time.text = TimeUtil.formatTime(item.last_reply_date.toString(), R.string.post_time, mContext)
        } else {
            time.text = TimeUtil.formatTime(item.last_reply_date.toString(), R.string.reply_time, mContext)
        }

        setImages(item, imageLayout, hideContent)
    }

    private fun setImages(item: CommonPostBean.ListBean, imageLayout: NineGridLayout, hideContent: Boolean?) {
        if (item.imageList != null) {
            val iterator = item.imageList.iterator()
            while (iterator.hasNext()) {
                if (Constant.SPLIT_LINES.contains(iterator.next())) {
                    iterator.remove()
                }
            }
        }
        if (!item.imageList.isNullOrEmpty() && hideContent == false) {
            imageLayout.visibility = View.VISIBLE
            imageLayout.setNineGridAdapter(NineImageAdapter(item.imageList))
        } else {
            imageLayout.visibility = View.GONE
        }
    }
}