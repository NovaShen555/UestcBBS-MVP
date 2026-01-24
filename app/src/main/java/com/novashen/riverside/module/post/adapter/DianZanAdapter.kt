package com.novashen.riverside.module.post.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.novashen.riverside.R
import com.novashen.riverside.entity.PostDetailBean
import com.novashen.riverside.helper.PreloadAdapter
import com.novashen.riverside.util.Constant
import com.novashen.riverside.util.load

/**
 * Created by sca_tl at 2023/4/13 14:45
 */
class DianZanAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<PostDetailBean.TopicBean.ZanListBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: PostDetailBean.TopicBean.ZanListBean) {
        val name = helper.getView<TextView>(R.id.name)
        val avatar = helper.getView<ImageView>(R.id.avatar)

        helper.addOnClickListener(R.id.avatar)
        name.text = item.username
        avatar.load(Constant.USER_AVATAR_URL + item.recommenduid)
    }
}