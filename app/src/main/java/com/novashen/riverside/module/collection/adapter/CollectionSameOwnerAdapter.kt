package com.novashen.riverside.module.collection.adapter

import android.content.res.ColorStateList
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.novashen.riverside.R
import com.novashen.riverside.entity.CollectionDetailBean
import com.novashen.riverside.helper.PreloadAdapter
import com.novashen.util.ColorUtil

/**
 * Created by sca_tl at 2023/5/4 20:17
 */
class CollectionSameOwnerAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<CollectionDetailBean.SameOwnerCollection, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: CollectionDetailBean.SameOwnerCollection) {
        super.convert(helper, item)
        val text = helper.getView<TextView>(R.id.text)

        text.apply {
            this.text = item.name
            this.setBackgroundResource(R.drawable.shape_collection_tag)
            this.backgroundTintList = ColorStateList.valueOf(ColorUtil.getAttrColor(mContext, R.attr.colorSurfaceVariant))
        }
    }
}