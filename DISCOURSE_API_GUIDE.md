# Discourse API 集成说明

## 概述

本项目已完成 Discourse API 的基础集成，可以从 river-side.cc 获取数据并转换为原有应用的数据格式。

## 已实现的功能

### 1. 数据模型

#### TopicListResponse
完整的 Discourse 帖子列表响应模型，包含：
- **users**: 所有相关用户信息列表
- **primary_groups**: 主要用户组信息
- **flair_groups**: 徽章组信息
- **topic_list**: 帖子列表及元数据
  - **topics**: 帖子数组
  - **can_create_topic**: 是否可以创建帖子
  - **per_page**: 每页数量
  - **top_tags**: 热门标签

#### Topic (帖子信息)
- id: 帖子ID
- title: 标题
- posts_count: 回复数
- views: 浏览数
- like_count: 点赞数
- created_at: 创建时间
- last_posted_at: 最后回复时间
- category_id: 板块ID
- posters: 发帖人列表
- tags: 标签列表
- is_hot: 是否热门
- pinned: 是否置顶

#### User (用户信息)
- id: 用户ID
- username: 用户名
- avatar_template: 头像模板
- trust_level: 信任等级
- flair_name: 徽章名称
- flair_url: 徽章图标URL

### 2. API 接口

#### DiscourseApiService
```java
// 获取 CSRF Token
Observable<CsrfTokenResponse> getCsrfToken()

// 登录
Observable<Response<LoginResponse>> login(String login, String password, int secondFactorMethod, String timezone)

// 获取最新回复的帖子列表
Observable<TopicListResponse> getLatestTopics()

// 获取最新创建的帖子列表
Observable<TopicListResponse> getNewTopics()

// 获取当前用户信息
Observable<Response<ResponseBody>> getCurrentUser()
```

### 3. 数据转换器

#### DiscourseDataConverter
将 Discourse 数据格式转换为原有应用的 CommonPostBean 格式：

```java
// 转换帖子列表
CommonPostBean convertToCommonPostBean(TopicListResponse response)

// 获取用户头像URL
String getAvatarUrl(String avatarTemplate, int size)

// 获取完整图片URL
String getFullImageUrl(String relativeUrl)
```

### 4. API 帮助类

#### DiscourseApiHelper
提供便捷的方法来获取和转换数据：

```java
// 获取最新回复的帖子（自动转换为 CommonPostBean）
Observable<CommonPostBean> getLatestTopicsAsCommonPost()

// 获取最新创建的帖子（自动转换为 CommonPostBean）
Observable<CommonPostBean> getNewTopicsAsCommonPost()
```

## 使用示例

### 1. 初始化

```java
// 获取 Discourse Retrofit 实例
DiscourseRetrofitUtil retrofitUtil = DiscourseRetrofitUtil.getInstance();
DiscourseApiService apiService = retrofitUtil.getApiService();

// 创建帮助类
DiscourseApiHelper apiHelper = new DiscourseApiHelper(apiService);
```

### 2. 登录

```java
// 步骤1: 获取 CSRF Token
apiService.getCsrfToken()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(csrfResponse -> {
        String csrfToken = csrfResponse.getCsrf();

        // 步骤2: 设置 CSRF Token 并登录
        retrofitUtil.getCsrfInterceptor().setManualCsrfToken(csrfToken);

        apiService.login("username", "password", 1, "Asia/Shanghai")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(response -> {
                if (response.isSuccessful()) {
                    LoginResponse.User user = response.body().getUser();
                    // 登录成功，清除手动设置的 CSRF token
                    retrofitUtil.getCsrfInterceptor().clearManualCsrfToken();
                    // 处理登录成功逻辑
                }
            });
    });
```

### 3. 获取帖子列表（原始格式）

```java
apiService.getLatestTopics()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(response -> {
        List<TopicListResponse.Topic> topics = response.getTopicList().getTopics();
        List<TopicListResponse.User> users = response.getUsers();

        // 处理帖子列表
        for (TopicListResponse.Topic topic : topics) {
            String title = topic.getTitle();
            int views = topic.getViews();
            int likes = topic.getLikeCount();

            // 获取发帖人信息
            if (!topic.getPosters().isEmpty()) {
                int userId = topic.getPosters().get(0).getUserId();
                TopicListResponse.User user = findUserById(users, userId);
                String username = user.getUsername();
                String avatar = user.getAvatarUrl(120);
            }
        }
    });
```

### 4. 获取帖子列表（转换为原有格式）

```java
apiHelper.getLatestTopicsAsCommonPost()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(commonPostBean -> {
        // 直接使用原有应用的数据格式
        List<CommonPostBean.ListBean> topics = commonPostBean.list;

        for (CommonPostBean.ListBean topic : topics) {
            int topicId = topic.topic_id;
            String title = topic.title;
            String userName = topic.user_nick_name;
            String avatar = topic.userAvatar;
            int replyCount = topic.replies;
            int views = topic.hits;
        }
    });
```

## 数据映射关系

### Discourse → CommonPostBean

| Discourse 字段 | CommonPostBean 字段 | 说明 |
|---------------|-------------------|------|
| id | topic_id | 帖子ID |
| title | title | 标题 |
| posts_count | replies | 回复数 |
| views | hits | 浏览数 |
| like_count | vote | 点赞数 |
| last_posted_at | last_reply_date | 最后回复时间 |
| category_id | board_id | 板块ID |
| image_url | pic_path | 图片URL |
| pinned | top | 是否置顶 |
| is_hot | essence | 是否精华 |

### 用户信息映射

| Discourse 字段 | CommonPostBean 字段 | 说明 |
|---------------|-------------------|------|
| user.id | user_id | 用户ID |
| user.username | user_nick_name | 用户名 |
| user.avatar_template | userAvatar | 头像URL（需要替换{size}） |

## 注意事项

### 1. CSRF Token 处理
- 首次登录需要手动获取并设置 CSRF Token
- 登录成功后，CSRF Token 会自动从 Cookie 中提取
- 所有需要认证的请求都会自动添加 CSRF Token

### 2. 请求头要求
- 必须包含 `X-Requested-With: XMLHttpRequest` 才能绕过 Cloudflare
- 已在拦截器中自动添加

### 3. Cookie 管理
- 使用 SimpleCookieJar 自动管理 Cookie
- 登录状态通过 Cookie 维持

### 4. 时间格式
- Discourse 使用 ISO 8601 格式：`2026-01-25T07:49:18.496Z`
- 转换器会自动转换为时间戳（毫秒）

### 5. 头像URL
- Discourse 返回的是模板：`/user_avatar/river-side.cc/username/{size}/xxx.png`
- 需要替换 `{size}` 为实际尺寸（如 120）
- 需要添加域名前缀：`https://river-side.cc`

## 下一步工作

### 需要实现的功能

1. **帖子详情**
   - API: `GET /t/{topic_id}.json`
   - 获取帖子内容和所有回复

2. **板块列表**
   - API: `GET /categories.json`
   - 获取所有板块信息

3. **发帖/回复**
   - API: `POST /posts.json`
   - 创建新帖子或回复

4. **用户信息**
   - API: `GET /u/{username}.json`
   - 获取用户详细信息

5. **搜索**
   - API: `GET /search.json?q={query}`
   - 搜索帖子和用户

6. **通知**
   - API: `GET /notifications.json`
   - 获取通知列表

7. **私信**
   - API: `GET /topics/private-messages/{username}.json`
   - 获取私信列表

8. **点赞/收藏**
   - API: `POST /post_actions.json`
   - 点赞或收藏帖子

## 测试

运行测试：
```bash
./gradlew test --tests com.novashen.riverside.api.discourse.DiscourseLoginTest.testLogin
```

测试会验证：
1. ✅ 获取 CSRF Token
2. ✅ 登录功能
3. ✅ 获取帖子列表
4. ✅ 用户信息解析

## 相关文件

- `DiscourseApiService.java` - API 接口定义
- `TopicListResponse.java` - 完整的响应数据模型
- `DiscourseDataConverter.java` - 数据转换器
- `DiscourseApiHelper.java` - API 帮助类
- `DiscourseRetrofitUtil.java` - Retrofit 工具类
- `DiscourseCsrfInterceptor.java` - CSRF Token 拦截器
- `SimpleCookieJar.java` - Cookie 管理器
