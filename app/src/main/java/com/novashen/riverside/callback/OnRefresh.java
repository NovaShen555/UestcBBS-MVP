package com.novashen.riverside.callback;

import com.scwang.smart.refresh.layout.api.RefreshLayout;


public interface OnRefresh {
    void onRefresh(RefreshLayout refreshLayout);
    void onLoadMore(RefreshLayout refreshLayout);
}
