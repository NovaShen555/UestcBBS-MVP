package com.novashen.riverside.module.magic.view;

import com.novashen.riverside.entity.MagicDetailBean;

public interface MagicDetailView {
    void onGetMagicDetailSuccess(MagicDetailBean magicDetailBean, String formhash);
    void onGetMagicDetailError(String msg);
    void onBuyMagicSuccess(String msg);
    void onBuyMagicError(String msg);
}
