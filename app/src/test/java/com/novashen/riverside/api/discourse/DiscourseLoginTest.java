package com.novashen.riverside.api.discourse;

import android.util.Log;

import com.novashen.riverside.api.discourse.entity.CreatePostRequest;
import com.novashen.riverside.api.discourse.entity.CreatePostResponse;
import com.novashen.riverside.api.discourse.entity.CsrfTokenResponse;
import com.novashen.riverside.api.discourse.entity.TopicListResponse;
import com.novashen.riverside.api.discourse.entity.LoginResponse;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Discourse 登录测试
 * 测试账号: NoahShen
 * 测试密码: SYHhyh240507
 */
public class DiscourseLoginTest {
    private static final String TAG = "DiscourseLoginTest";
    private static final String TEST_USERNAME = "NoahShen";
    private static final String TEST_PASSWORD = "SYHhyh240507";
    private static final String TIMEZONE = "Asia/Shanghai";
    private static final int TEST_TOPIC_ID = 2324; // 测试帖子ID

    private DiscourseRetrofitUtil retrofitUtil;
    private DiscourseApiService apiService;

    @Before
    public void setup() {
        retrofitUtil = DiscourseRetrofitUtil.getInstance();
        apiService = retrofitUtil.getApiService();

        // 清除之前的 session
        retrofitUtil.clearSession();
    }

    @Test
    public void testLogin() throws InterruptedException {
        System.out.println("========== 开始测试 Discourse 登录 ==========");

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] loginSuccess = {false};

        // 步骤1: 获取 CSRF Token
        System.out.println("\n步骤1: 获取 CSRF Token...");
        apiService.getCsrfToken()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<CsrfTokenResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("开始获取 CSRF Token");
                    }

                    @Override
                    public void onNext(CsrfTokenResponse csrfTokenResponse) {
                        String csrfToken = csrfTokenResponse.getCsrf();
                        System.out.println("✓ 成功获取 CSRF Token: " + csrfToken.substring(0, Math.min(20, csrfToken.length())) + "...");

                        // 步骤2: 设置 CSRF Token 并登录
                        System.out.println("\n步骤2: 使用 CSRF Token 登录...");
                        retrofitUtil.getCsrfInterceptor().setManualCsrfToken(csrfToken);

                        apiService.login(TEST_USERNAME, TEST_PASSWORD, 1, TIMEZONE)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(new Observer<Response<LoginResponse>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        System.out.println("开始登录...");
                                    }

                                    @Override
                                    public void onNext(Response<LoginResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            LoginResponse loginResponse = response.body();
                                            LoginResponse.User user = loginResponse.getUser();

                                            System.out.println("✓ 登录成功！");
                                            System.out.println("  用户ID: " + user.getId());
                                            System.out.println("  用户名: " + user.getUsername());
                                            System.out.println("  邮箱: " + user.getEmail());
                                            System.out.println("  信任等级: " + user.getTrustLevel());
                                            System.out.println("  管理员: " + user.isAdmin());
                                            System.out.println("  版主: " + user.isModerator());

                                            // 清除手动设置的 CSRF token，后续从 cookie 中获取
                                            retrofitUtil.getCsrfInterceptor().clearManualCsrfToken();

                                            // 步骤3: 测试获取最新回复帖子列表
                                            System.out.println("\n步骤3: 测试获取最新回复帖子列表...");
                                            testGetLatestTopics(latch, loginSuccess);
                                        } else {
                                            System.err.println("✗ 登录失败: " + response.code() + " " + response.message());
                                            try {
                                                if (response.errorBody() != null) {
                                                    System.err.println("错误详情: " + response.errorBody().string());
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            latch.countDown();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        System.err.println("✗ 登录出错: " + e.getMessage());
                                        e.printStackTrace();
                                        latch.countDown();
                                    }

                                    @Override
                                    public void onComplete() {
                                        System.out.println("登录请求完成");
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("✗ 获取 CSRF Token 失败: " + e.getMessage());
                        e.printStackTrace();

                        // 打印响应内容以便调试
                        System.err.println("\n尝试直接获取响应内容...");
                        testGetCsrfTokenRaw(latch);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("CSRF Token 请求完成");
                    }
                });

        // 等待测试完成（最多60秒）
        boolean completed = latch.await(60, TimeUnit.SECONDS);

        System.out.println("\n========== 测试结果 ==========");
        if (completed && loginSuccess[0]) {
            System.out.println("✓ 所有测试通过！");
        } else if (!completed) {
            System.err.println("✗ 测试超时");
        } else {
            System.err.println("✗ 测试失败");
        }
        System.out.println("==============================\n");

        // 断言测试结果
        assert completed : "测试超时";
        assert loginSuccess[0] : "登录或获取帖子列表失败";
    }

    private void testGetCsrfTokenRaw(CountDownLatch latch) {
        // 使用 ResponseBody 直接获取原始响应
        apiService.getCsrfTokenRaw()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<okhttp3.ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("尝试获取原始响应...");
                    }

                    @Override
                    public void onNext(okhttp3.ResponseBody responseBody) {
                        try {
                            String rawResponse = responseBody.string();
                            System.out.println("原始响应内容: " + rawResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("获取原始响应失败: " + e.getMessage());
                        e.printStackTrace();
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void testGetLatestTopics(CountDownLatch latch, boolean[] loginSuccess) {
        apiService.getLatestTopics()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<TopicListResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("开始获取最新回复帖子...");
                    }

                    @Override
                    public void onNext(TopicListResponse response) {
                        if (response.getTopicList() != null && response.getTopicList().getTopics() != null) {
                            int topicCount = response.getTopicList().getTopics().size();
                            int userCount = response.getUsers() != null ? response.getUsers().size() : 0;

                            System.out.println("✓ 成功获取最新回复帖子列表");
                            System.out.println("  帖子数量: " + topicCount);
                            System.out.println("  用户数量: " + userCount);
                            System.out.println("  每页数量: " + response.getTopicList().getPerPage());

                            // 显示前3个帖子
                            System.out.println("\n前3个帖子:");
                            for (int i = 0; i < Math.min(3, topicCount); i++) {
                                TopicListResponse.Topic topic = response.getTopicList().getTopics().get(i);
                                System.out.println("  " + (i + 1) + ". " + topic.getTitle());
                                System.out.println("     ID: " + topic.getId() +
                                                 ", 回复: " + topic.getPostsCount() +
                                                 ", 浏览: " + topic.getViews() +
                                                 ", 点赞: " + topic.getLikeCount());

                                // 显示发帖人信息
                                if (topic.getPosters() != null && !topic.getPosters().isEmpty()) {
                                    TopicListResponse.Poster originalPoster = topic.getPosters().get(0);
                                    TopicListResponse.User user = findUserById(response.getUsers(), originalPoster.getUserId());
                                    if (user != null) {
                                        System.out.println("     作者: " + user.getUsername() +
                                                         " (信任等级: " + user.getTrustLevel() + ")");
                                    }
                                }
                            }

                            loginSuccess[0] = true;
                        } else {
                            System.err.println("✗ 帖子列表为空");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("✗ 获取最新回复帖子列表失败: " + e.getMessage());
                        e.printStackTrace();
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("获取最新回复帖子列表完成");
                        latch.countDown();
                    }
                });
    }

    /**
     * 根据用户ID查找用户信息
     */
    private TopicListResponse.User findUserById(List<TopicListResponse.User> users, int userId) {
        if (users == null) return null;
        for (TopicListResponse.User user : users) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    /**
     * 测试完整流程：CSRF Token -> 登录 -> 发表评论
     */
    @Test
    public void testCompleteFlow() throws InterruptedException {
        System.out.println("========== 开始测试完整流程：CSRF -> 登录 -> 发表评论 ==========");

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] testSuccess = {false};

        // 步骤1: 获取 CSRF Token
        System.out.println("\n步骤1: 获取 CSRF Token...");
        apiService.getCsrfToken()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<CsrfTokenResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("开始获取 CSRF Token");
                    }

                    @Override
                    public void onNext(CsrfTokenResponse csrfTokenResponse) {
                        String csrfToken = csrfTokenResponse.getCsrf();
                        System.out.println("✓ 成功获取 CSRF Token: " + csrfToken.substring(0, Math.min(20, csrfToken.length())) + "...");

                        // 步骤2: 设置 CSRF Token 并登录
                        System.out.println("\n步骤2: 使用 CSRF Token 登录...");
                        retrofitUtil.getCsrfInterceptor().setManualCsrfToken(csrfToken);

                        apiService.login(TEST_USERNAME, TEST_PASSWORD, 1, TIMEZONE)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(new Observer<Response<LoginResponse>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        System.out.println("开始登录...");
                                    }

                                    @Override
                                    public void onNext(Response<LoginResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            LoginResponse loginResponse = response.body();
                                            LoginResponse.User user = loginResponse.getUser();

                                            System.out.println("✓ 登录成功！");
                                            System.out.println("  用户ID: " + user.getId());
                                            System.out.println("  用户名: " + user.getUsername());

                                            // 从登录响应中提取新的 CSRF token
                                            String newCsrfToken = response.raw().header("x-csrf-token");
                                            if (newCsrfToken != null && !newCsrfToken.isEmpty()) {
                                                retrofitUtil.getCsrfInterceptor().setManualCsrfToken(newCsrfToken);
                                                System.out.println("✓ 从登录响应中提取新 CSRF Token: " +
                                                        newCsrfToken.substring(0, Math.min(20, newCsrfToken.length())) + "...");
                                            } else {
                                                // 如果响应中没有新 token，清除手动设置的 token
                                                retrofitUtil.getCsrfInterceptor().clearManualCsrfToken();
                                            }

                                            // 步骤3: 发表测试评论
                                            System.out.println("\n步骤3: 发表测试评论...");
                                            testCreatePost(latch, testSuccess);
                                        } else {
                                            System.err.println("✗ 登录失败: " + response.code() + " " + response.message());
                                            try {
                                                if (response.errorBody() != null) {
                                                    System.err.println("错误详情: " + response.errorBody().string());
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            latch.countDown();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        System.err.println("✗ 登录出错: " + e.getMessage());
                                        e.printStackTrace();
                                        latch.countDown();
                                    }

                                    @Override
                                    public void onComplete() {
                                        System.out.println("登录请求完成");
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("✗ 获取 CSRF Token 失败: " + e.getMessage());
                        e.printStackTrace();
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("CSRF Token 请求完成");
                    }
                });

        // 等待测试完成（最多60秒）
        boolean completed = latch.await(60, TimeUnit.SECONDS);

        System.out.println("\n========== 测试结果 ==========");
        if (completed && testSuccess[0]) {
            System.out.println("✓ 完整流程测试通过！");
        } else if (!completed) {
            System.err.println("✗ 测试超时");
        } else {
            System.err.println("✗ 测试失败");
        }
        System.out.println("==============================\n");

        // 断言测试结果
        assert completed : "测试超时";
        assert testSuccess[0] : "完整流程测试失败";
    }

    /**
     * 测试发表评论
     */
    private void testCreatePost(CountDownLatch latch, boolean[] testSuccess) {
        // 创建测试评论内容
        String testContent = "测试评论 - " + System.currentTimeMillis();
        CreatePostRequest request = new CreatePostRequest(testContent, TEST_TOPIC_ID);

        System.out.println("准备发表评论到帖子 #" + TEST_TOPIC_ID);
        System.out.println("评论内容: " + testContent);

        apiService.createPost(request)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Response<CreatePostResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("开始发表评论...");
                    }

                    @Override
                    public void onNext(Response<CreatePostResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            CreatePostResponse postResponse = response.body();
                            System.out.println("✓ 评论发表成功！");
                            System.out.println("  评论ID: " + postResponse);

                            testSuccess[0] = true;
                        } else {
                            System.err.println("✗ 发表评论失败: " + response.code() + " " + response.message());
                            try {
                                if (response.errorBody() != null) {
                                    System.err.println("错误详情: " + response.errorBody().string());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("✗ 发表评论出错: " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("发表评论请求完成");
                        latch.countDown();
                    }
                });
    }
}
