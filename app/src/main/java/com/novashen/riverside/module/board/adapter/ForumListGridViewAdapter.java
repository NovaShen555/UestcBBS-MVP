package com.novashen.riverside.module.board.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.novashen.riverside.R;
import com.novashen.riverside.entity.ForumListBean;
import com.novashen.riverside.helper.glidehelper.GlideLoader4Common;
import com.novashen.riverside.module.board.view.BoardActivity;
import com.novashen.riverside.util.Constant;
import com.novashen.riverside.util.SharePrefUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/21 17:07
 */
public class ForumListGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<ForumListBean.ListBean.BoardListBean> boardListBeans;
    private int parentBoardId;
    private static final String ASSET_PREFIX = "file:///android_asset/";
    private static final Map<String, Boolean> ASSET_EXISTS_CACHE = new ConcurrentHashMap<>();

    public ForumListGridViewAdapter(Context context, List<ForumListBean.ListBean.BoardListBean> listBeans, int parentBoardId) {
        this.context = context;
        this.boardListBeans = listBeans;
        this.parentBoardId = parentBoardId;
    }

    @Override
    public int getCount() {
        return boardListBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return boardListBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {

            view = LayoutInflater.from(context).inflate(R.layout.item_forum_list_gridview, new RelativeLayout(context));
            holder = new ViewHolder();

            holder.name = view.findViewById(R.id.forum_list_right_name);
            holder.desc = view.findViewById(R.id.forum_list_right_desc);
            holder.imageView = view.findViewById(R.id.forum_list_right_img);
            holder.rootLayout = view.findViewById(R.id.item_forum_list_gridview_rootlayout);

            holder.rootLayout.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, BoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, boardListBeans.get(i).board_id);
                intent.putExtra(Constant.IntentKey.PARENT_BOARD_ID, parentBoardId);
                intent.putExtra(Constant.IntentKey.BOARD_NAME, boardListBeans.get(i).board_name);
                context.startActivity(intent);
            });

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        ForumListBean.ListBean.BoardListBean boardListBean = boardListBeans.get(i);
        holder.name.setText(boardListBean.board_name + "(" + boardListBean.td_posts_num + ")");
//        holder.desc.setText(context.getResources().getString(R.string.today_posts, boardListBean.td_posts_num));

        String imgPath = !TextUtils.isEmpty(boardListBean.board_img)
                ? boardListBean.board_img
                : SharePrefUtil.getBoardImg(context, boardListBean.board_id);

        if (!TextUtils.isEmpty(imgPath) && imgPath.startsWith(ASSET_PREFIX)) {
            String assetPath = imgPath.substring(ASSET_PREFIX.length());
            if (assetExists(assetPath)) {
                GlideLoader4Common.simpleLoad(context, imgPath, holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_boardlist1);
            }
        } else if (!TextUtils.isEmpty(imgPath)) {
            GlideLoader4Common.simpleLoad(context, imgPath, holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_boardlist1);
        }

        return view;
    }

    private boolean assetExists(String assetPath) {
        Boolean cached = ASSET_EXISTS_CACHE.get(assetPath);
        if (cached != null) {
            return cached;
        }
        boolean exists;
        try {
            context.getAssets().open(assetPath).close();
            exists = true;
        } catch (Exception e) {
            exists = false;
        }
        ASSET_EXISTS_CACHE.put(assetPath, exists);
        return exists;
    }

    public static class ViewHolder {
        View rootLayout;
        TextView name, desc;
        ImageView imageView;
    }
}
