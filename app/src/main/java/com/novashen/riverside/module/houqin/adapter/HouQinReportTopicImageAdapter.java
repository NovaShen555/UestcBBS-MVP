package com.novashen.riverside.module.houqin.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novashen.riverside.R;
import com.novashen.riverside.helper.glidehelper.GlideLoader4Common;

/**
 * author: sca_tl
 * date: 2020/10/25 17:58
 * description:
 */
public class HouQinReportTopicImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public HouQinReportTopicImageAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        helper.addOnClickListener(R.id.image);
        GlideLoader4Common.simpleLoad(mContext, item+"", helper.getView(R.id.image));
        helper.getView(R.id.delete_btn).setVisibility(View.GONE);
    }
}
