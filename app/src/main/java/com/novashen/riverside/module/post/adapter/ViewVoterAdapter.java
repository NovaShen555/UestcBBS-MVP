package com.novashen.riverside.module.post.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novashen.riverside.R;
import com.novashen.riverside.entity.ViewVoterBean;
import com.novashen.riverside.helper.glidehelper.GlideLoader4Common;

public class ViewVoterAdapter extends BaseQuickAdapter<ViewVoterBean, BaseViewHolder> {
    public ViewVoterAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ViewVoterBean item) {
        helper.setText(R.id.name, item.name)
                .addOnClickListener(R.id.avatar);

        GlideLoader4Common.simpleLoad(mContext, item.avatar, helper.getView(R.id.avatar));
    }
}
