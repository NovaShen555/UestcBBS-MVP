package com.novashen.riverside.module.board.view;

import com.novashen.riverside.entity.CommonPostBean;

public interface BoardPostView {
    void onGetBoardPostSuccess(CommonPostBean singleBoardBean);
    void onGetBoardPostError(String msg);

    void onPaySuccess(String msg);
    void onPayError(String msg);
}
