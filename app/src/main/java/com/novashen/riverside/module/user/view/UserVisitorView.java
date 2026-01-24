package com.novashen.riverside.module.user.view;

public interface UserVisitorView {
    void onDeleteVisitedHistorySuccess(String msg, int position);
    void onDeleteVisitedHistoryError(String msg);
}
