package com.novashen.riverside.module.user.view;

import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.novashen.riverside.R;
import com.novashen.riverside.base.BaseEvent;
import com.novashen.riverside.base.BaseFragment;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.module.user.presenter.UserMainPagePresenter;
import com.novashen.riverside.util.Constant;
import com.novashen.riverside.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;


public class UserMainPageFragment extends BaseFragment implements UserMainPageView{

    TextView isOnlineTv, onLineTimeTv, LastLoginTv, ageTv;
    View moreInfoLayout;

    UserMainPagePresenter userMainPagePresenter;

    int uid;
    String username;

    public static UserMainPageFragment getInstance(Bundle bundle) {
        UserMainPageFragment userMainPageFragment = new UserMainPageFragment();
        userMainPageFragment.setArguments(bundle);
        return userMainPageFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            uid = bundle.getInt(Constant.IntentKey.USER_ID, Integer.MAX_VALUE);
            username = bundle.getString(Constant.IntentKey.USER_NAME);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_user_main_page;
    }

    @Override
    protected void findView() {
        isOnlineTv = view.findViewById(R.id.user_main_page_online_status);
        onLineTimeTv = view.findViewById(R.id.user_main_page_online_time);
        LastLoginTv = view.findViewById(R.id.user_main_page_last_login_time);
        ageTv = view.findViewById(R.id.user_main_page_register_age);
        moreInfoLayout = view.findViewById(R.id.user_main_page_view_more_info);
    }

    @Override
    protected void initView() {
        userMainPagePresenter = (UserMainPagePresenter) presenter;

        moreInfoLayout.setOnClickListener(this);

        if (username != null && !username.isEmpty()) {
             userMainPagePresenter.getDiscourseUserSpace(username, mActivity);
        } else {
             userMainPagePresenter.getUserSpace(uid, mActivity);
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UserMainPagePresenter();
    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.user_main_page_view_more_info) {
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.VIEW_USER_MORE_INFO));
        }
    }

    @Override
    public void onGetUserSpaceSuccess(String onLineTime, String registerTime, String lastLoginTime, String ipLocation) {
        if (isOnlineTv != null) isOnlineTv.setVisibility(View.GONE);
        onLineTimeTv.setText(onLineTime);

        LastLoginTv.setText(TimeUtil.formatTime(TimeUtil.getMilliSecond(lastLoginTime, "yyyy-MM-dd HH:mm") + "", R.string.post_time1, mActivity));
        ageTv.setText(TimeUtil.caclDays(registerTime, "yyyy-MM-dd HH:mm") + "天（" + registerTime + "注册）");
    }

    @Override
    public void onGetUserSpaceError(String msg) {

    }
}