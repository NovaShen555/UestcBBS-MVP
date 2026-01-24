package com.novashen.riverside.module.message.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novashen.riverside.R;
import com.novashen.riverside.entity.AtMsgBean;
import com.novashen.riverside.helper.glidehelper.GlideLoader4Common;
import com.novashen.riverside.util.TimeUtil;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 13:08
 */
public class AtMeMsgAdapter extends BaseQuickAdapter<AtMsgBean.BodyBean.DataBean, BaseViewHolder> {

    public AtMeMsgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, AtMsgBean.BodyBean.DataBean item) {
        helper.setText(R.id.user_name, item.user_name)
                .setText(R.id.reply_content, item.reply_content.replaceAll("\r\n", ""))
                .setText(R.id.board_name, "来自板块:" + item.board_name)
                .setText(R.id.subject_title, item.topic_subject)
                .setText(R.id.subject_content, item.topic_content.trim())
                .setText(R.id.reply_date,
                        TimeUtil.formatTime(item.replied_date, R.string.post_time1, mContext))
                .addOnClickListener(R.id.user_icon)
                .addOnClickListener(R.id.board_name);

        GlideLoader4Common.simpleLoad(mContext, item.icon, helper.getView(R.id.user_icon));
    }
}
