package com.novashen.riverside.module.magic.view;

import com.novashen.riverside.entity.UseMagicBean;

public interface UseMagicView {
    void onGetUseMagicDetailSuccess(UseMagicBean useMagicBean, String formhash);
    void onGetUseMagicDetailError(String msg);
    void onUseMagicSuccess(String msg);
    void onUseMagicError(String msg);
}
