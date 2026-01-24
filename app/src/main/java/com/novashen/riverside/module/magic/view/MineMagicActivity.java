package com.novashen.riverside.module.magic.view;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.novashen.riverside.R;
import com.novashen.riverside.base.BaseActivity;
import com.novashen.riverside.base.BaseEvent;
import com.novashen.riverside.callback.OnRefresh;
import com.novashen.riverside.entity.MineMagicBean;
import com.novashen.riverside.module.magic.adapter.MineMagicAdapter;
import com.novashen.riverside.module.magic.presenter.MineMagicPresenter;
import com.novashen.riverside.util.Constant;
import com.novashen.riverside.util.RefreshUtil;
import com.novashen.riverside.util.TimeUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;

public class MineMagicActivity extends BaseActivity<MineMagicPresenter> implements MineMagicView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MineMagicAdapter mineMagicAdapter;
    private TextView hint;
    private Toolbar toolbar;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_mine_magic;
    }

    @Override
    protected void findView() {
        refreshLayout = findViewById(R.id.mine_magic_refresh);
        recyclerView = findViewById(R.id.mine_magic_rv);
        hint = findViewById(R.id.mine_magic_hint);
        toolbar = findViewById(R.id.mine_magic_toolbar);
    }

    @Override
    protected void initView() {
        super.initView();
        mineMagicAdapter = new MineMagicAdapter(R.layout.item_mine_magic);
        recyclerView.setAdapter(mineMagicAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.autoRefresh(0, 300, 1 ,false);
    }

    @Override
    protected MineMagicPresenter initPresenter() {
        return new MineMagicPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        mineMagicAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_mine_magic_use_btn) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.IntentKey.MAGIC_ID, mineMagicAdapter.getData().get(position).magicId);
                UseMagicFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                presenter.getMineMagic();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onGetMineMagicSuccess(MineMagicBean mineMagicBean) {
        hint.setText("");
        mineMagicAdapter.setNewData(mineMagicBean.itemLists);
        recyclerView.scheduleLayoutAnimation();
        refreshLayout.finishRefresh(true);
    }

    @Override
    public void onGetMineMagicError(String msg) {
        mineMagicAdapter.setNewData(new ArrayList<>());
        refreshLayout.finishRefresh(false);
        hint.setText(msg);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.USE_MAGIC_SUCCESS) {
            refreshLayout.autoRefresh(0, 300, 1 ,false);
        }
    }
}