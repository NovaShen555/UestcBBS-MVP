package com.novashen.riverside.module.board.adapter;

import android.widget.GridView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novashen.riverside.R;
import com.novashen.riverside.entity.ForumListBean;
import com.novashen.riverside.util.SharePrefUtil;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 16:31
 */
public class ForumListRightAdapter extends BaseQuickAdapter<ForumListBean.ListBean, BaseViewHolder> {

    public ForumListRightAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ForumListBean.ListBean item) {
        helper.setText(R.id.forum_list_right_title, item.board_category_name);
        ForumListGridViewAdapter forumListGridViewAdapter = new ForumListGridViewAdapter(mContext, getData().get(helper.getLayoutPosition()).board_list);
        GridView gridView = helper.getView(R.id.forum_list_right_gridview);
        gridView.setNumColumns(SharePrefUtil.getBoardListColumns(mContext));
        gridView.setAdapter(forumListGridViewAdapter);
        gridView.requestFocus();
    }
}
