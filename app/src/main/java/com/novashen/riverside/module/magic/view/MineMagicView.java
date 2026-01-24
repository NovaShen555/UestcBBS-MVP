package com.novashen.riverside.module.magic.view;

import com.novashen.riverside.entity.MineMagicBean;

public interface MineMagicView {
    void onGetMineMagicSuccess(MineMagicBean mineMagicBean);
    void onGetMineMagicError(String msg);
}
