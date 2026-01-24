package com.novashen.riverside.module.user.view;

public interface ModifyAvatarView {
    void onGetParaSuccess(String agent, String input);
    void onGetParaError(String msg);
    void onUploadSuccess(String msg);
    void onUploadError(String msg);
}
