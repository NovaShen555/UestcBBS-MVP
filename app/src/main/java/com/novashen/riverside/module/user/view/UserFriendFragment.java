package com.novashen.riverside.module.user.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.novashen.riverside.R;
import android.util.Log;

import com.novashen.riverside.annotation.UserFriendType;
import com.novashen.riverside.base.BaseBottomFragment;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.widget.MyLinearLayoutManger;
import com.novashen.riverside.api.discourse.entity.DiscourseFollowUser;
import com.novashen.riverside.module.user.adapter.UserFriendAdapter;
import com.novashen.riverside.module.user.presenter.UserFriendPresenter;
import com.novashen.riverside.util.Constant;
import com.novashen.riverside.util.SharePrefUtil;
import com.novashen.widget.bottomsheet.ViewPagerBottomSheetBehavior;

import java.util.List;


public class UserFriendFragment extends BaseBottomFragment implements UserFriendView{

    private static final String TAG = "UserFriendFragment";

    private RecyclerView recyclerView;
    private UserFriendAdapter userFriendAdapter;
    private TextView title, hint;
    private ProgressBar progressBar;

    private UserFriendPresenter userFriendPresenter;

    private int uid;
    private String type, name;

    public static UserFriendFragment getInstance(Bundle bundle) {
        UserFriendFragment userFriendFragment = new UserFriendFragment();
        userFriendFragment.setArguments(bundle);
        return userFriendFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            uid = bundle.getInt(Constant.IntentKey.USER_ID, Integer.MAX_VALUE);
            name = bundle.getString(Constant.IntentKey.USER_NAME);
            type = bundle.getString(Constant.IntentKey.TYPE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_bottom_user_friend;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.fragment_bottom_user_friend_rv);
        title = view.findViewById(R.id.fragment_bottom_user_friend_title);
        hint = view.findViewById(R.id.fragment_bottom_user_friend_hint);
        progressBar = view.findViewById(R.id.fragment_bottom_user_friend_progressbar);
    }

    @Override
    protected void initView() {
        userFriendPresenter = (UserFriendPresenter) presenter;
        mBehavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);

        String username = name;
        if (username == null || username.trim().length() == 0) {
            if (uid == SharePrefUtil.getUid(mActivity)) {
                username = SharePrefUtil.getDiscourseUsername(mActivity);
            }
        }

        if (uid == SharePrefUtil.getUid(mActivity)) {
            if (UserFriendType.TYPE_FOLLOW.equals(type)) {
                title.setText("我关注的");
            } else if (UserFriendType.TYPE_FOLLOWED.equals(type)){
                title.setText("我的粉丝");
            } else if (UserFriendType.TYPE_FRIEND.equals(type)) {
                title.setText("我的好友");
            }
        } else {
            if (UserFriendType.TYPE_FOLLOW.equals(type)) {
                title.setText(name + "关注的");
            } else if (UserFriendType.TYPE_FOLLOWED.equals(type)){
                title.setText(name + "的粉丝");
            } else if (UserFriendType.TYPE_FRIEND.equals(type)) {
                title.setText(name + "的好友");
            }
        }
//        title.setText(uid == SharePrefUtil.getUid(mActivity) ? UserFriendType.TYPE_FOLLOW.equals(type) ? "我关注的" : "我的粉丝"
//                : UserFriendType.TYPE_FOLLOW.equals(type) ? name + "关注的" : name + "的粉丝");

        userFriendAdapter = new UserFriendAdapter(R.layout.item_user_friend);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(userFriendAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));

        if (username == null || username.trim().length() == 0) {
            progressBar.setVisibility(View.GONE);
            hint.setText("缺少用户名，无法获取列表");
            return;
        }
        userFriendPresenter.getUserFriend(username, type, mActivity);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UserFriendPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        userFriendAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_user_friend_root_layout) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                DiscourseFollowUser user = userFriendAdapter.getData().get(position);
                intent.putExtra(Constant.IntentKey.USER_ID, user.getId());
                intent.putExtra(Constant.IntentKey.USER_NAME, user.getUsername());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onGetUserFriendSuccess(List<DiscourseFollowUser> userList) {
        progressBar.setVisibility(View.GONE);
        userFriendAdapter.setNewData(userList);
        hint.setText(userFriendAdapter.getData().size() == 0 ? "啊哦，还没有数据" : "");

        Log.d(TAG, "List set, size=" + (userList == null ? 0 : userList.size()));
        for (int i = 0; i < userList.size(); i++) {
            DiscourseFollowUser user = userList.get(i);
            if (user != null && user.getUsername() != null && user.getUsername().trim().length() > 0) {
                userFriendPresenter.getUserLastSeen(user.getUsername(), i);
            }
        }
    }

    @Override
    public void onGetUserFriendError(String msg) {
        progressBar.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onUserLastSeenLoaded(int position, String lastSeenAt) {
        if (position < 0 || position >= userFriendAdapter.getData().size()) {
            return;
        }
        DiscourseFollowUser user = userFriendAdapter.getData().get(position);
        if (user != null) {
            user.setLastSeenAt(lastSeenAt);
            userFriendAdapter.notifyItemChanged(position);
            Log.d(TAG, "Update item: pos=" + position + ", user=" + user.getUsername()
                    + ", lastSeenAt=" + lastSeenAt);
        }
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.92;
    }
}
