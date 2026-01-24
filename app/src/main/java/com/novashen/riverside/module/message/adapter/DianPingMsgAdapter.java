package com.novashen.riverside.module.message.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novashen.riverside.R;
import com.novashen.riverside.entity.DianPingMsgBean;
import com.novashen.riverside.util.TimeUtil;
import com.novashen.riverside.helper.glidehelper.GlideLoader4Common;
import com.novashen.riverside.util.Constant;

/**
 * author: sca_tl
 * date: 2021/4/18 18:17
 * description:
 */
public class DianPingMsgAdapter extends BaseQuickAdapter<DianPingMsgBean.BodyBean.DataBean, BaseViewHolder> {

    public DianPingMsgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DianPingMsgBean.BodyBean.DataBean item) {
        helper.setText(R.id.user_name, item.comment_user_name)
                .setText(R.id.reply_content, "点评了你的帖子，点击查看")
                .setText(R.id.board_name, "来自板块:" + item.board_name)
                .setText(R.id.subject_title, item.topic_subject)
                .setText(R.id.subject_content, item.reply_content.trim())
                .setText(R.id.reply_date,
                        TimeUtil.formatTime(item.replied_date, R.string.post_time1, mContext))
                .addOnClickListener(R.id.user_icon)
                .addOnClickListener(R.id.board_name);

        GlideLoader4Common.simpleLoad(mContext, Constant.USER_AVATAR_URL + item.comment_user_id, helper.getView(R.id.user_icon));
    }
}
