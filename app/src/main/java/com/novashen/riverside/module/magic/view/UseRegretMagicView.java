package com.novashen.riverside.module.magic.view;

import com.novashen.riverside.entity.UseRegretMagicBean;

public interface UseRegretMagicView {
    void onGetMagicDetailSuccess(UseRegretMagicBean useRegretMagicBean, String formhash);
    void onGetMagicDetailError(String msg);
    void onUseMagicSuccess(String msg);
    void onUseMagicError(String msg);
}
