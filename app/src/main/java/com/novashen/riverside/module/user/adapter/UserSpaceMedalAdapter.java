package com.novashen.riverside.module.user.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novashen.riverside.R;
import com.novashen.riverside.helper.glidehelper.GlideLoader4Common;

public class UserSpaceMedalAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public UserSpaceMedalAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        GlideLoader4Common.simpleLoad(mContext, item, helper.getView(R.id.item_user_space_medal_img));
    }
}
