package com.novashen.riverside.module.search.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.novashen.riverside.R
import com.novashen.riverside.entity.SearchUserBean
import com.novashen.riverside.helper.PreloadAdapter
import com.novashen.riverside.util.TimeUtil
import com.novashen.riverside.util.load

/**
 * Created by sca_tl at 2023/4/4 10:10
 */
class SearchUserAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<SearchUserBean.BodyBean.ListBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: SearchUserBean.BodyBean.ListBean) {
        super.convert(helper, item)
        helper
            .setText(R.id.search_user_name, item.name)
            .setText(R.id.search_user_last_login, TimeUtil.formatTime(item.dateline, R.string.last_login_time, mContext))

        helper.getView<ImageView>(R.id.search_user_icon).load(item.icon)
    }

}