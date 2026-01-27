package com.novashen.riverside.api.discourse.interceptor;

import com.novashen.riverside.App;
import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil;
import com.novashen.riverside.api.discourse.entity.CsrfTokenResponse;
import com.novashen.riverside.api.discourse.entity.LoginResponse;
import com.novashen.riverside.util.SharePrefUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Discourse 认证拦截器
 * 当请求返回 403 时，自动重新登录并重试请求
 */
public class DiscourseAuthInterceptor implements Interceptor {
    private static final String TAG = "DiscourseAuthInterceptor";
    private static boolean isRefreshing = false;
    private static final Object lock = new Object();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Response response = chain.proceed(originalRequest);

        // 如果是 403 Forbidden 或 422 Unprocessable Entity 且不是登录或 CSRF 请求，尝试重新登录
        // 422 错误可能是由于 session 过期导致的，也需要重新登录
        if ((response.code() == 403 || response.code() == 422) &&
            !originalRequest.url().encodedPath().contains("/session") &&
            !isRefreshing) {

            logDebug("Received " + response.code() + ", attempting to re-login");

            synchronized (lock) {
                if (isRefreshing) {
                    // 其他线程正在刷新，直接返回
                    return response;
                }
                isRefreshing = true;
            }

            try {
                // 关闭原始响应
                response.close();

                // 尝试重新登录
                boolean loginSuccess = reLogin();

                if (loginSuccess) {
                    logDebug("Re-login successful, retrying original request");

                    // 获取新的 CSRF token
                    String newCsrfToken = DiscourseRetrofitUtil.getInstance()
                            .getCsrfInterceptor()
                            .getCurrentCsrfToken();

                    // 重新构建请求，使用新的 CSRF token
                    Request.Builder requestBuilder = originalRequest.newBuilder()
                            .removeHeader("x-csrf-token")
                            .removeHeader("discourse-present");

                    if (newCsrfToken != null && !newCsrfToken.isEmpty()) {
                        requestBuilder.header("x-csrf-token", newCsrfToken);
                        requestBuilder.header("discourse-present", "true");
                        logDebug("Added new CSRF token to retry request");
                    }

                    Request newRequest = requestBuilder.build();
                    // 重新执行请求
                    return chain.proceed(newRequest);
                } else {
                    logWarn("Re-login failed, returning 403");
                    // 登录失败，重新执行请求获取 403 响应
                    return chain.proceed(originalRequest);
                }
            } finally {
                synchronized (lock) {
                    isRefreshing = false;
                }
            }
        }

        return response;
    }

    /**
     * 重新登录（同步方式）
     * @return 登录是否成功
     */
    private boolean reLogin() {
        try {
            // 从 SharedPreferences 获取保存的用户名和密码
            String username = SharePrefUtil.getDiscourseUsername(App.getContext());
            String password = SharePrefUtil.getDiscoursePassword(App.getContext());

            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                logWarn("No saved credentials found");
                return false;
            }

            logDebug("Attempting to re-login with saved credentials for user: " + username);

            DiscourseRetrofitUtil retrofitUtil = DiscourseRetrofitUtil.getInstance();

            // 清除旧的会话
            retrofitUtil.clearSession();

            // 步骤1: 获取 CSRF Token（同步调用）
            CsrfTokenResponse csrfResponse = retrofitUtil.getApiService()
                    .getCsrfToken()
                    .blockingFirst();

            if (csrfResponse == null || csrfResponse.getCsrf() == null) {
                logWarn("Failed to get CSRF token");
                return false;
            }

            String csrfToken = csrfResponse.getCsrf();
            logDebug("Got CSRF token: " + csrfToken.substring(0, Math.min(20, csrfToken.length())) + "...");

            // 步骤2: 设置 CSRF Token
            retrofitUtil.getCsrfInterceptor().setManualCsrfToken(csrfToken);

            // 步骤3: 执行登录（同步调用）
            retrofit2.Response<LoginResponse> loginResponse = retrofitUtil.getApiService()
                    .login(username, password, 1, "Asia/Shanghai")
                    .blockingFirst();

            if (loginResponse.isSuccessful() && loginResponse.body() != null) {
                logDebug("Re-login successful");

                // 从登录响应中提取新的 CSRF token
                String newCsrfFromResponse = loginResponse.raw().header("x-csrf-token");
                if (newCsrfFromResponse != null && !newCsrfFromResponse.isEmpty()) {
                    // 手动设置新的 CSRF token，这样 getCurrentCsrfToken() 就能获取到它
                    retrofitUtil.getCsrfInterceptor().setManualCsrfToken(newCsrfFromResponse);
                    logDebug("Extracted new CSRF token from login response: " +
                            newCsrfFromResponse.substring(0, Math.min(20, newCsrfFromResponse.length())) + "...");
                }

                return true;
            } else {
                logWarn("Re-login failed with code: " + loginResponse.code());
                return false;
            }

        } catch (Exception e) {
            logError("Re-login error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 日志输出（兼容单元测试）
     */
    private void logDebug(String message) {
        try {
            android.util.Log.d(TAG, message);
        } catch (RuntimeException e) {
            System.out.println(TAG + ": " + message);
        }
    }

    private void logWarn(String message) {
        try {
            android.util.Log.w(TAG, message);
        } catch (RuntimeException e) {
            System.out.println(TAG + " [WARN]: " + message);
        }
    }

    private void logError(String message) {
        try {
            android.util.Log.e(TAG, message);
        } catch (RuntimeException e) {
            System.out.println(TAG + " [ERROR]: " + message);
        }
    }
}
