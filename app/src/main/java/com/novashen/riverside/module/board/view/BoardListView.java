package com.novashen.riverside.module.board.view;

import com.novashen.riverside.entity.ForumListBean;

public interface BoardListView {
    void onGetBoardListSuccess(ForumListBean forumListBean);
    void onGetBoardListError(String msg);
}
