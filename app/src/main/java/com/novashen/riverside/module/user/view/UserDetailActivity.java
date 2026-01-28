package com.novashen.riverside.module.user.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jaeger.library.StatusBarUtil;
import com.novashen.riverside.R;
import com.novashen.riverside.annotation.ToastType;
import com.novashen.riverside.annotation.UserFriendType;
import com.novashen.riverside.base.BaseActivity;
import com.novashen.riverside.base.BaseEvent;
import com.novashen.riverside.entity.BlackUserBean;
import com.novashen.riverside.entity.FollowUserBean;
import com.novashen.riverside.entity.ModifyPswBean;
import com.novashen.riverside.entity.ModifySignBean;
import com.novashen.riverside.entity.UserDetailBean;
import com.novashen.riverside.entity.UserFriendBean;
import com.novashen.riverside.entity.VisitorsBean;
import com.novashen.riverside.manager.BlackListManager;
import com.novashen.riverside.module.credit.view.CreditTransferFragment;
import com.novashen.riverside.module.message.view.PrivateChatActivity;
import com.novashen.riverside.module.report.ReportFragment;
import com.novashen.riverside.module.user.adapter.UserDetailViewPagerAdapter;
import com.novashen.riverside.module.user.adapter.UserSpaceMedalAdapter;
import com.novashen.riverside.module.user.presenter.UserDetailPresenter;
import com.novashen.riverside.util.Constant;
import com.novashen.riverside.util.ForumUtil;
import com.novashen.riverside.util.SharePrefUtil;
import com.novashen.riverside.util.TimeUtil;
import com.novashen.riverside.widget.MyLinearLayoutManger;
import com.novashen.util.ExtensionKt;
import com.novashen.util.ImageUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDetailActivity extends BaseActivity<UserDetailPresenter> implements UserDetailView, AppBarLayout.OnOffsetChangedListener{

    private RelativeLayout userInfoRl;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private LottieAnimationView loading;
    private ImageView background, chatBtn, blackBtn;
    private ImageView avatar;
    private TextView userNameTv, userFollowed, userFollow, userLevel, hint;
    private TextView favoriteBtn, favoriteToolbarBtn;
    private Button blackedBtn;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private RecyclerView userMedalRv;
    private View actionLayout;
    private UserSpaceMedalAdapter userSpaceMedalAdapter;

    private UserDetailBean userDetailBean;
    private List<VisitorsBean> visitorsBeans;

    private int userId;
    private String userName;
    private int tabIndex;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        userId = intent.getIntExtra(Constant.IntentKey.USER_ID, 0);
        tabIndex = intent.getIntExtra(Constant.IntentKey.POSITION, 0);
        userName = intent.getStringExtra(Constant.IntentKey.USER_NAME);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_user_detail;
    }

    @Override
    protected void findView() {
        appBarLayout = findViewById(R.id.user_detail_app_bar);
        actionLayout = findViewById(R.id.user_detail_action_layout);
        toolbar = findViewById(R.id.toolbar);
        coordinatorLayout = findViewById(R.id.user_detail_coor_layout);
        userInfoRl = findViewById(R.id.user_detail_info_rl);
        background = findViewById(R.id.user_detail_user_background);
        avatar = findViewById(R.id.user_detail_user_icon);
        userNameTv = findViewById(R.id.user_detail_user_name);
        userFollowed = findViewById(R.id.user_detail_followed_num);
        userFollow = findViewById(R.id.user_detail_follow_num);
        userLevel = findViewById(R.id.user_detail_user_level);
        favoriteBtn = findViewById(R.id.user_detail_favorite_btn);
        chatBtn = findViewById(R.id.user_detail_chat_btn);
        blackBtn = findViewById(R.id.user_detail_black_btn);
        hint = findViewById(R.id.user_detail_hint);
        tabLayout = findViewById(R.id.user_detail_indicator);
        viewPager2 = findViewById(R.id.user_detail_viewpager);
        loading = findViewById(R.id.user_detail_loading);
        userMedalRv = findViewById(R.id.user_detail_user_medal_rv);
        blackedBtn = findViewById(R.id.user_detail_blacked_btn);
        favoriteToolbarBtn = findViewById(R.id.user_detail_favorite_toolbar_btn);
    }

    @Override
    protected void initView() {
        super.initView();

        favoriteBtn.setOnClickListener(this);
        favoriteToolbarBtn.setOnClickListener(this::onClickListener);
        chatBtn.setOnClickListener(this);
        blackBtn.setOnClickListener(this);
        userLevel.setOnClickListener(this);
        userFollowed.setOnClickListener(this);
        userFollow.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(this);
        avatar.setOnClickListener(this::onClickListener);
        blackedBtn.setOnClickListener(this::onClickListener);

        if (userId == SharePrefUtil.getUid(this)) {
            actionLayout.setVisibility(View.GONE);
        }

        viewPager2.setOffscreenPageLimit(4);
        ExtensionKt.desensitize(viewPager2);
        viewPager2.setAdapter(new UserDetailViewPagerAdapter(this, userId, userName));
        viewPager2.setCurrentItem(0, false);

        final String[] titles = {"主页", "发表", "回复", "收藏"};

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) ->
                tab.setText(titles[position])
        ).attach();

        if (tabIndex >= 0 && tabIndex < 4) {
            viewPager2.setCurrentItem(tabIndex, false);
        }

        // 只使用Discourse API获取用户信息
        if (!TextUtils.isEmpty(userName)) {
            presenter.getDiscourseUserDetail(userName, this);
        } else if (userId == SharePrefUtil.getUid(this)) {
            // 查看自己的主页，使用当前登录的用户名
            String currentUsername = SharePrefUtil.getDiscourseUsername(this);
            if (!TextUtils.isEmpty(currentUsername)) {
                presenter.getDiscourseUserDetail(currentUsername, this);
            } else {
                onGetUserDetailError("未找到Discourse用户名");
            }
        } else {
            onGetUserDetailError("需要提供用户名");
        }
    }

    @Override
    protected UserDetailPresenter initPresenter() {
        return new UserDetailPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.user_detail_favorite_btn || view.getId() == R.id.user_detail_favorite_toolbar_btn) {
            presenter.followUser(userId, userDetailBean.is_follow == 1 ? "unfollow" : "follow", this);
        }
        if (view.getId() == R.id.user_detail_chat_btn) {
            Intent intent = new Intent(this, PrivateChatActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, userId);
            intent.putExtra(Constant.IntentKey.USER_NAME, userDetailBean.name);
            startActivity(intent);
        }
        if (view.getId() == R.id.user_detail_black_btn || view.getId() == R.id.user_detail_blacked_btn) {
            if (userDetailBean.is_black == 0) {
                presenter.showBlackConfirmDialog(this, userId);
            } else {
                presenter.blackUser(userId, "delblack", this);
            }
        }
        if (view.getId() == R.id.user_detail_user_level) {
            presenter.showUserInfo(userDetailBean, false, this);
        }
        if (view.getId() == R.id.user_detail_followed_num) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.USER_ID, userId);
            bundle.putString(Constant.IntentKey.USER_NAME, userDetailBean.name);
            bundle.putString(Constant.IntentKey.TYPE, UserFriendType.TYPE_FOLLOWED);
            UserFriendFragment.getInstance(bundle)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.user_detail_follow_num) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.USER_ID, userId);
            bundle.putString(Constant.IntentKey.USER_NAME, userDetailBean.name);
            bundle.putString(Constant.IntentKey.TYPE, UserFriendType.TYPE_FOLLOW);
            UserFriendFragment.getInstance(bundle)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.user_detail_user_icon) {
            if (userId == SharePrefUtil.getUid(this)) {
                startActivity(new Intent(this, ModifyAvatarActivity.class));
            } else {
                List<String> urls = new ArrayList<>();
                urls.add(userDetailBean.icon);
                com.novashen.riverside.util.ImageUtil.showImages(this, urls,0);
            }
        }
    }

    @Override
    public void onGetSpaceByNameSuccess(int uid) {
        // 已废弃，不再使用旧API
    }

    @Override
    public void onGetSpaceByNameError(String msg) {
        hint.setText(msg);
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onGetUserDetailSuccess(UserDetailBean userDetailBean) {

        this.userDetailBean = userDetailBean;

        tabLayout.getTabAt(1).setText("发表(" + userDetailBean.topic_num + ")");
        tabLayout.getTabAt(2).setText("回复(" + userDetailBean.reply_posts_num + ")");

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(600);
        coordinatorLayout.startAnimation(alphaAnimation);
        coordinatorLayout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);

        userNameTv.setText(userDetailBean.name);
        userFollow.setText(String.valueOf("关注：" + userDetailBean.friend_num));
        userFollowed.setText(String.valueOf("粉丝：" + userDetailBean.follow_num));
        toolbar.setTitle(userDetailBean.name);

        if (userId != SharePrefUtil.getUid(this)) setBlackStatus();

        if (!TextUtils.isEmpty(userDetailBean.userTitle)) {
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(userDetailBean.userTitle);
            userLevel.setText(matcher.find() ? matcher.group(2) : userDetailBean.userTitle);
            userLevel.setBackgroundTintList(ColorStateList.valueOf(ForumUtil.getLevelColor(this, userDetailBean.userTitle)));
        }

        Glide.with(this).load(userDetailBean.icon).into(avatar);
        Glide.with(this).load(userDetailBean.icon).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                Bitmap bitmap = resource instanceof GifDrawable ?  ((GifDrawable) resource).getFirstFrame() : ImageUtil.drawable2Bitmap(resource);
                if (bitmap != null) {
                    background.setImageBitmap(ImageUtil.blur(UserDetailActivity.this, bitmap, 5));
                }
            }
        });

    }

    @Override
    public void onGetUserDetailError(String msg) {
        hint.setText(msg);
        loading.setVisibility(View.GONE);
    }

    private void setBlackStatus() {
        if (userDetailBean.is_black == 1) {
            blackedBtn.setVisibility(View.VISIBLE);
            favoriteBtn.setVisibility(View.GONE);
            favoriteToolbarBtn.setVisibility(View.GONE);
            chatBtn.setVisibility(View.GONE);
            blackBtn.setVisibility(View.GONE);

            blackedBtn.setText("解除黑名单");
            blackedBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF3C3C")));
        } else {
            blackedBtn.setVisibility(View.GONE);
            favoriteBtn.setVisibility(View.VISIBLE);
            favoriteToolbarBtn.setVisibility(View.VISIBLE);
            chatBtn.setVisibility(View.VISIBLE);
            blackBtn.setVisibility(View.VISIBLE);

            favoriteBtn.setText(userDetailBean.is_follow == 1 ? "已关注" : "+ 关注");
            favoriteToolbarBtn.setText(userDetailBean.is_follow == 1 ? "已关注" : "+ 关注");
            blackBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor(userDetailBean.is_black == 1 ? "#FF3C3C" : "#bbbbbb")));
        }
    }

    @Override
    public void onFollowUserSuccess(FollowUserBean followUserBean) {
        showToast(followUserBean.head.errInfo, ToastType.TYPE_SUCCESS);
        userDetailBean.is_follow = userDetailBean.is_follow == 1 ? 0 : 1;
        favoriteBtn.setText(userDetailBean.is_follow == 1 ? "已关注" : "+ 关注");
        favoriteToolbarBtn.setText(userDetailBean.is_follow == 1 ? "已关注" : "+ 关注");
    }

    @Override
    public void onFollowUserError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onBlackUserSuccess(BlackUserBean blackUserBean) {
        showToast(blackUserBean.head.errInfo, ToastType.TYPE_SUCCESS);
        blackBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor(userDetailBean.is_black == 0 ? "#FF3C3C" : "#bbbbbb")));
        userDetailBean.is_black = userDetailBean.is_black == 1 ? 0 : 1;

        //将该用户数据从本地删除或写入
        if (userDetailBean.is_black == 1) {//拉黑
            BlackListManager.Companion.getINSTANCE().add(userId, userDetailBean.name);
        } else {//取消拉黑
            BlackListManager.Companion.getINSTANCE().delete(userId);
        }

        setBlackStatus();
    }

    @Override
    public void onBlackUserError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onModifySignSuccess(ModifySignBean modifySignBean, String sign) {
        userDetailBean.sign = sign;
        showToast(modifySignBean.head.errInfo, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onModifySignError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onModifyPswSuccess(ModifyPswBean modifyPswBean) {
        showToast(modifyPswBean.head.errInfo, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onModifyPswError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onGetUserSpaceSuccess(List<VisitorsBean> visitorsBeans, List<String> medalImages) {
        this.visitorsBeans = visitorsBeans;

        if (medalImages == null || medalImages.size() == 0) {
            userMedalRv.setVisibility(View.GONE);
        } else {
            userMedalRv.setVisibility(View.VISIBLE);
            userSpaceMedalAdapter = new UserSpaceMedalAdapter(R.layout.item_user_space_medal);
            MyLinearLayoutManger myLinearLayoutManger = new MyLinearLayoutManger(this);
            myLinearLayoutManger.setOrientation(LinearLayoutManager.HORIZONTAL);
            userMedalRv.setLayoutManager(myLinearLayoutManger);
            userMedalRv.setAdapter(userSpaceMedalAdapter);
            userSpaceMedalAdapter.setNewData(medalImages);
        }

    }

    @Override
    public void onGetUserSpaceError(String msg) {

    }

    @Override
    public void onGetUserFriendSuccess(UserFriendBean userFriendBean) {
    }

    @Override
    public void onGetUserFriendError(String msg) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int scrollRange = appBarLayout.getTotalScrollRange();
        float alpha = 1 - (1.0f * (- i)) / scrollRange;
        userInfoRl.setAlpha(alpha);
        toolbar.setAlpha(1-alpha);
        favoriteToolbarBtn.setVisibility(alpha == 0 ? SharePrefUtil.getUid(this) == userId ? View.GONE : View.VISIBLE : View.GONE);
    }

    @Override
    protected void onOptionsSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_user_detail_modify_profile) {
            presenter.showModifyInfoDialog(this);
        }

        if (userDetailBean != null) {
            if (item.getItemId() == R.id.menu_user_detail_transfer_credit) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.IntentKey.USER_NAME, userDetailBean.name);
                CreditTransferFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }
        }

        if (item.getItemId() == R.id.menu_user_detail_report) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.IntentKey.TYPE, "user");
            bundle.putInt(Constant.IntentKey.ID, userId);
            ReportFragment.Companion.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }

    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.DELETE_MINE_VISITOR_HISTORY_SUCCESS) {
            for (int i = 0; i < visitorsBeans.size(); i ++) {
                if (visitorsBeans.get(i).visitorUid == SharePrefUtil.getUid(this)) {
                    visitorsBeans.remove(i);
                    break;
                }
            }
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.VIEW_USER_MORE_INFO) {
            presenter.showUserInfo(userDetailBean, false, this);
        }
    }
}
