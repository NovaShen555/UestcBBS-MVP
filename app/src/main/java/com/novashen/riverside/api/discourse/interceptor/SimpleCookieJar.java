package com.novashen.riverside.api.discourse.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 简单的 Cookie 管理器
 * 用于存储和管理 Discourse 的 Cookie
 */
public class SimpleCookieJar implements CookieJar {
    private static final String TAG = "SimpleCookieJar";
    private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        String host = url.host();
        List<Cookie> existingCookies = cookieStore.get(host);
        if (existingCookies == null) {
            existingCookies = new ArrayList<>();
        }

        // 更新或添加 cookie
        for (Cookie newCookie : cookies) {
            // 移除同名的旧 cookie
            existingCookies.removeIf(cookie -> cookie.name().equals(newCookie.name()));
            existingCookies.add(newCookie);
            logDebug("Saved cookie: " + newCookie.name() + " for host: " + host);
        }

        cookieStore.put(host, existingCookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        String host = url.host();
        List<Cookie> cookies = cookieStore.get(host);
        if (cookies != null) {
            // 移除过期的 cookie
            List<Cookie> validCookies = new ArrayList<>();
            for (Cookie cookie : cookies) {
                if (cookie.expiresAt() > System.currentTimeMillis()) {
                    validCookies.add(cookie);
                }
            }
            logDebug("Loaded " + validCookies.size() + " cookies for host: " + host);
            return validCookies;
        }
        return new ArrayList<>();
    }

    /**
     * 清除所有 Cookie
     */
    public void clear() {
        cookieStore.clear();
        logDebug("Cleared all cookies");
    }

    /**
     * 清除指定主机的 Cookie
     */
    public void clear(String host) {
        cookieStore.remove(host);
        logDebug("Cleared cookies for host: " + host);
    }

    /**
     * 日志输出（兼容单元测试）
     */
    private void logDebug(String message) {
        try {
            android.util.Log.d(TAG, message);
        } catch (RuntimeException e) {
            // 在单元测试环境中，Android Log 会抛出异常，使用 System.out
            System.out.println(TAG + ": " + message);
        }
    }
}
