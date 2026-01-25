package com.novashen.riverside.api.discourse;

import android.util.Log;

import com.novashen.riverside.api.discourse.entity.CsrfTokenResponse;
import com.novashen.riverside.api.discourse.entity.LatestTopicsResponse;
import com.novashen.riverside.api.discourse.entity.LoginResponse;

import org.junit.Before;
import org.junit.Test;

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

                        apiService.login(TEST_USERNAME, TEST_PASSWORD, 1, TIMEZONE, csrfToken)
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

                                            // 步骤3: 测试获取最新帖子列表
                                            System.out.println("\n步骤3: 测试获取最新帖子列表...");
                                            testGetLatestTopics(latch, loginSuccess);
                                        } else {
                                            System.err.println("✗ 登录失败: " + response.code() + " " + response.message());
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

    private void testGetLatestTopics(CountDownLatch latch, boolean[] loginSuccess) {
        apiService.getLatestTopics()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<LatestTopicsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("开始获取最新帖子...");
                    }

                    @Override
                    public void onNext(LatestTopicsResponse response) {
                        if (response.getTopicList() != null && response.getTopicList().getTopics() != null) {
                            int topicCount = response.getTopicList().getTopics().size();
                            System.out.println("✓ 成功获取最新帖子列表，共 " + topicCount + " 个帖子");

                            // 显示前3个帖子
                            System.out.println("\n前3个帖子:");
                            for (int i = 0; i < Math.min(3, topicCount); i++) {
                                LatestTopicsResponse.Topic topic = response.getTopicList().getTopics().get(i);
                                System.out.println("  " + (i + 1) + ". " + topic.getTitle());
                                System.out.println("     ID: " + topic.getId() +
                                                 ", 回复: " + topic.getPostsCount() +
                                                 ", 浏览: " + topic.getViews() +
                                                 ", 点赞: " + topic.getLikeCount());
                            }

                            loginSuccess[0] = true;
                        } else {
                            System.err.println("✗ 帖子列表为空");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("✗ 获取帖子列表失败: " + e.getMessage());
                        e.printStackTrace();
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("获取帖子列表完成");
                        latch.countDown();
                    }
                });
    }
}
