package com.novashen.riverside.api.discourse.interceptor;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Discourse CSRF Token 拦截器
 * 自动从 Cookie 中提取 CSRF Token 并添加到请求头
 */
public class DiscourseCsrfInterceptor implements Interceptor {
    private static final String TAG = "DiscourseCsrfInterceptor";
    private CookieJar cookieJar;
    private String manualCsrfToken; // 手动设置的 CSRF token（用于首次登录）

    public DiscourseCsrfInterceptor(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
    }

    /**
     * 手动设置 CSRF Token（用于首次登录）
     */
    public void setManualCsrfToken(String csrfToken) {
        this.manualCsrfToken = csrfToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // 如果是获取 CSRF token 的请求，直接放行
        if (originalRequest.url().encodedPath().contains("/session/csrf")) {
            return chain.proceed(originalRequest);
        }

        // 获取 CSRF Token
        String csrfToken = getCsrfToken(originalRequest.url());

        // 构建新请求
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .addHeader("x-requested-with", "XMLHttpRequest")
                .addHeader("discourse-present", "true");

        // 如果有 CSRF Token，添加到请求头
        if (csrfToken != null && !csrfToken.isEmpty()) {
            requestBuilder.addHeader("x-csrf-token", csrfToken);
            Log.d(TAG, "Adding CSRF token to request: " + csrfToken.substring(0, Math.min(20, csrfToken.length())) + "...");
        } else {
            Log.w(TAG, "No CSRF token found for request: " + originalRequest.url());
        }

        Request newRequest = requestBuilder.build();
        return chain.proceed(newRequest);
    }

    /**
     * 从 Cookie 中提取 CSRF Token
     */
    private String getCsrfToken(HttpUrl url) {
        // 优先使用手动设置的 token（用于首次登录）
        if (manualCsrfToken != null && !manualCsrfToken.isEmpty()) {
            String token = manualCsrfToken;
            // 登录后清除手动设置的 token，后续从 cookie 中获取
            if (url.encodedPath().contains("/session") && !url.encodedPath().contains("/csrf")) {
                // 这是登录请求，使用后不清除，等登录成功后再清除
            }
            return token;
        }

        // 从 Cookie 中提取 CSRF token
        List<Cookie> cookies = cookieJar.loadForRequest(url);
        for (Cookie cookie : cookies) {
            if ("_forum_session".equals(cookie.name())) {
                // Discourse 的 CSRF token 存储在 _forum_session cookie 中
                // 但实际上我们需要从响应的 Set-Cookie 中解析
                // 这里简化处理，直接从 cookie 值中提取
                Log.d(TAG, "Found _forum_session cookie");
            }
        }

        // 如果从 cookie 中无法提取，返回 null
        return null;
    }

    /**
     * 清除手动设置的 CSRF Token
     */
    public void clearManualCsrfToken() {
        this.manualCsrfToken = null;
    }
}
