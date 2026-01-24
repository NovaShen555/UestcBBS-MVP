package com.novashen.riverside.module.post.adapter

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.novashen.riverside.R
import com.novashen.riverside.entity.RateUserBean
import com.novashen.riverside.helper.PreloadAdapter
import com.novashen.riverside.util.Constant
import com.novashen.riverside.util.load

/**
 * Created by sca_tl at 2023/4/20 16:45
 */
class PingFenAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<RateUserBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: RateUserBean) {
        helper
            .setText(R.id.name, item.userName)
            .setText(R.id.time, item.time)
            .setText(R.id.reason, item.reason)
            .addOnClickListener(R.id.root_layout)

        val credit = helper.getView<TextView>(R.id.credit)
        val avatar = helper.getView<ImageView>(R.id.avatar)

        credit.text = item.credit
        credit.setTextColor(if (item.credit.contains("水滴"))
            Color.parseColor("#108EE9") else Color.parseColor("#D3CC00"))

        avatar.load(Constant.USER_AVATAR_URL + item.uid)
    }

}