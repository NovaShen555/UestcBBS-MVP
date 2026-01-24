package com.novashen.riverside.module.magic.view;

import com.novashen.riverside.entity.MagicShopBean;

public interface MagicShopView {
    void onGetMagicShopSuccess(MagicShopBean magicShopBean);
    void onGetMagicShopError(String msg);
}
