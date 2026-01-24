package com.novashen.riverside.module.message.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novashen.riverside.R;
import com.novashen.riverside.entity.PrivateMsgBean;
import com.novashen.riverside.helper.glidehelper.GlideLoader4Common;
import com.novashen.riverside.util.TimeUtil;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 16:25
 */
public class PrivateMsgAdapter extends BaseQuickAdapter<PrivateMsgBean.BodyBean.ListBean, BaseViewHolder> {

    public PrivateMsgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivateMsgBean.BodyBean.ListBean item) {
        helper.setText(R.id.user_name, item.toUserName)
                .setText(R.id.content, TextUtils.isEmpty(item.lastSummary) ? "[图片]" : item.lastSummary)
                .setText(R.id.time, TimeUtil.formatTime(item.lastDateline, R.string.post_time1, mContext))
                .addOnClickListener(R.id.user_icon);
        GlideLoader4Common.simpleLoad(mContext, item.toUserAvatar, helper.getView(R.id.user_icon));

        helper.getView(R.id.unread_img).setVisibility(item.isNew == 1 ? View.VISIBLE : View.GONE);
    }
}
