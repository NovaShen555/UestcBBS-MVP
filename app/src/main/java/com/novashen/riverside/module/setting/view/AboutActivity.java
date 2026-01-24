package com.novashen.riverside.module.setting.view;

import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;

import com.novashen.riverside.R;
import com.novashen.riverside.base.BaseActivity;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.util.SystemUtil;

public class AboutActivity extends BaseActivity {

    private TextView version;
    private Toolbar toolbar;
    private ImageView appIcon;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_about;
    }

    @Override
    protected void findView() {
        version = findViewById(R.id.about_app_version);
        toolbar = findViewById(R.id.toolbar);
        appIcon = findViewById(R.id.about_app_icon);
    }

    @Override
    protected void initView() {
        super.initView();
        version.setText(SystemUtil.getVersionName(this));
        appIcon.setOnClickListener(this::onClickListener);

        AboutFragment aboutFragment = new AboutFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.about_framelayout, aboutFragment)
                .commit();
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}
