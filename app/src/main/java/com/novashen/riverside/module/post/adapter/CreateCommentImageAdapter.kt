package com.novashen.riverside.module.post.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.novashen.riverside.R
import com.novashen.riverside.helper.PreloadAdapter
import com.novashen.riverside.util.load

/**
 * Created by sca_tl at 2023/4/20 16:12
 */
class CreateCommentImageAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<String, BaseViewHolder>(layoutResId, onPreload) {

    fun delete(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun convert(helper: BaseViewHolder, item: String) {
        val image = helper.getView<ImageView>(R.id.image)
        val deleteBtn = helper.getView<ImageView>(R.id.delete_btn)
        helper.addOnClickListener(R.id.delete_btn)
        image.load(item)
    }

}