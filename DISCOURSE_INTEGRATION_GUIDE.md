# Discourse API 集成指南

## 概述

本指南说明如何将 Discourse API 集成到现有的清水河畔 BBS 应用中，实现从 river-side.cc 获取数据。

## 已创建的组件

### 1. 登录相关

#### DiscourseLoginView
登录视图接口，定义登录成功和失败的回调。

#### DiscourseLoginPresenter
登录 Presenter，处理登录逻辑。

#### DiscourseAccountModel
账号模型，封装 Discourse 登录 API 调用。

### 2. 帖子列表相关

#### DiscourseLatestPostPresenter
帖子列表 Presenter，提供两个方法：
- `getLatestTopics()` - 获取最新回复的帖子
- `getNewTopics()` - 获取最新创建的帖子

#### DiscourseHomeModel
数据模型，封装 Discourse 帖子列表 API 调用。

## 集成步骤

### 方式一：在现有 Fragment 中切换数据源

#### 1. 修改 LatestPostFragment

在 `LatestPostFragment.java` 中添加 Discourse 支持：

```java
public class LatestPostFragment extends BaseFragment implements LatestPostView, IHomeRefresh {

    // 添加 Discourse Presenter
    private DiscourseLatestPostPresenter mDiscoursePresenter;

    // 添加切换标志
    private boolean useDiscourse = true; // 设置为 true 使用 Discourse

    @Override
    protected BasePresenter initPresenter() {
        if (useDiscourse) {
            return new DiscourseLatestPostPresenter();
        } else {
            return new LatestPostPresenter();
        }
    }

    @Override
    protected void setOnRefreshListener() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (useDiscourse) {
                    // 使用 Discourse API
                    mDiscoursePresenter = (DiscourseLatestPostPresenter) presenter;
                    mDiscoursePresenter.getLatestTopics(); // 或 getNewTopics()
                } else {
                    // 使用原有 API
                    mLatestPostPresenter.getSimplePostList(1, 20, "publish", mActivity);
                }
            }
        });
    }
}
```

### 方式二：创建新的 Discourse Fragment

创建一个新的 Fragment 专门用于 Discourse 数据：

```java
package com.novashen.riverside.module.home.view;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.novashen.riverside.R;
import com.novashen.riverside.base.BaseFragment;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.entity.CommonPostBean;
import com.novashen.riverside.module.home.presenter.DiscourseLatestPostPresenter;
import com.novashen.riverside.module.post.adapter.CommonPostAdapter;
import com.novashen.riverside.widget.MyLinearLayoutManger;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

/**
 * Discourse 最新帖子 Fragment
 */
public class DiscourseLatestPostFragment extends BaseFragment implements LatestPostView {

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private CommonPostAdapter adapter;
    private DiscourseLatestPostPresenter mPresenter;

    public static DiscourseLatestPostFragment getInstance(Bundle bundle) {
        DiscourseLatestPostFragment fragment = new DiscourseLatestPostFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_latest_post;
    }

    @Override
    protected void findView() {
        mPresenter = (DiscourseLatestPostPresenter) presenter;
        recyclerView = view.findViewById(R.id.home_rv);
        refreshLayout = view.findViewById(R.id.home_refresh);
    }

    @Override
    protected void initView() {
        adapter = new CommonPostAdapter(R.layout.item_common_post, "", null);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        refreshLayout.autoRefresh(10, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new DiscourseLatestPostPresenter();
    }

    @Override
    protected void setOnRefreshListener() {
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            mPresenter.getLatestTopics(); // 获取最新回复的帖子
        });

        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 实现加载更多
            refreshLayout.finishLoadMore();
        });
    }

    @Override
    public void getSimplePostDataSuccess(CommonPostBean commonPostBean) {
        if (commonPostBean != null && commonPostBean.list != null) {
            adapter.setNewData(commonPostBean.list);
            refreshLayout.finishRefresh(true);
        }
    }

    @Override
    public void getSimplePostDataError(String errorMsg) {
        showToast(errorMsg);
        refreshLayout.finishRefresh(false);
    }

    // 实现其他 LatestPostView 接口方法...
}
```

### 方式三：添加登录功能

在登录界面中使用 Discourse 登录：

```java
public class LoginActivity extends AppCompatActivity implements DiscourseLoginView {

    private DiscourseLoginPresenter loginPresenter;
    private EditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPresenter = new DiscourseLoginPresenter();
        loginPresenter.attachView(this);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                loginPresenter.login(username, password);
            } else {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoginSuccess(LoginResponse.User user) {
        Toast.makeText(this, "登录成功！欢迎 " + user.getUsername(), Toast.LENGTH_SHORT).show();

        // 保存登录状态
        SharePrefUtil.setLogin(this, true);
        SharePrefUtil.setName(this, user.getUsername());
        SharePrefUtil.setUid(this, String.valueOf(user.getId()));

        // 跳转到主页
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginError(String errorMsg) {
        Toast.makeText(this, "登录失败: " + errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loginPresenter != null) {
            loginPresenter.detachView();
        }
    }
}
```

## 快速开始

### 1. 最简单的方式：直接替换数据源

在 `LatestPostFragment.java` 的 `setOnRefreshListener()` 方法中：

```java
@Override
protected void setOnRefreshListener() {
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            // 注释掉原有的 API 调用
            // mLatestPostPresenter.getSimplePostList(1, 20, "publish", mActivity);

            // 使用 Discourse API
            DiscourseLatestPostPresenter discoursePresenter = new DiscourseLatestPostPresenter();
            discoursePresenter.attachView(LatestPostFragment.this);
            discoursePresenter.getLatestTopics();
        }
    });
}
```

### 2. 测试登录功能

创建一个简单的测试 Activity：

```java
// 在 MainActivity 或其他地方添加测试按钮
Button testLoginBtn = findViewById(R.id.test_login_btn);
testLoginBtn.setOnClickListener(v -> {
    DiscourseLoginPresenter loginPresenter = new DiscourseLoginPresenter();
    loginPresenter.attachView(new DiscourseLoginView() {
        @Override
        public void onLoginSuccess(LoginResponse.User user) {
            Toast.makeText(MainActivity.this,
                "登录成功: " + user.getUsername(),
                Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoginError(String errorMsg) {
            Toast.makeText(MainActivity.this,
                "登录失败: " + errorMsg,
                Toast.LENGTH_SHORT).show();
        }
    });

    loginPresenter.login("NoahShen", "SYHhyh240507");
});
```

## 配置选项

### 切换数据源

在 `SharePrefUtil` 或 `Constant` 中添加配置：

```java
public class Constant {
    public static class Config {
        // 是否使用 Discourse API
        public static final boolean USE_DISCOURSE = true;
    }
}
```

然后在代码中根据配置切换：

```java
if (Constant.Config.USE_DISCOURSE) {
    // 使用 Discourse API
    discoursePresenter.getLatestTopics();
} else {
    // 使用原有 API
    latestPostPresenter.getSimplePostList(1, 20, "publish", mActivity);
}
```

## 数据兼容性

Discourse API 返回的数据已经通过 `DiscourseDataConverter` 转换为 `CommonPostBean` 格式，可以直接使用现有的 `CommonPostAdapter` 显示。

### 字段映射

| Discourse | CommonPostBean | 说明 |
|-----------|---------------|------|
| id | topic_id | 帖子ID |
| title | title | 标题 |
| posts_count | replies | 回复数 |
| views | hits | 浏览数 |
| like_count | vote | 点赞数 |
| user.username | user_nick_name | 用户名 |
| user.avatar | userAvatar | 头像 |

## 注意事项

1. **CSRF Token**: 登录时会自动处理 CSRF Token，无需手动管理
2. **Cookie 管理**: 登录状态通过 Cookie 自动维持
3. **请求头**: 已自动添加 `X-Requested-With: XMLHttpRequest` 绕过 Cloudflare
4. **数据格式**: 所有数据已转换为原有格式，可直接使用现有 Adapter

## 测试

运行单元测试验证功能：

```bash
./gradlew test --tests com.novashen.riverside.api.discourse.DiscourseLoginTest.testLogin
```

## 下一步

1. 实现帖子详情页面
2. 实现发帖/回复功能
3. 实现搜索功能
4. 实现用户信息页面
5. 实现通知功能

## 相关文件

- `DiscourseLoginPresenter.java` - 登录 Presenter
- `DiscourseAccountModel.java` - 账号模型
- `DiscourseLatestPostPresenter.java` - 帖子列表 Presenter
- `DiscourseHomeModel.java` - 主页数据模型
- `DiscourseDataConverter.java` - 数据转换器
- `DiscourseApiHelper.java` - API 帮助类
