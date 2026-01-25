package com.novashen.riverside.api.discourse.interceptor;

import android.util.Log;

import java.io.IOException;
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
            Log.d(TAG, "Saved cookie: " + newCookie.name() + " for host: " + host);
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
            Log.d(TAG, "Loaded " + validCookies.size() + " cookies for host: " + host);
            return validCookies;
        }
        return new ArrayList<>();
    }

    /**
     * 清除所有 Cookie
     */
    public void clear() {
        cookieStore.clear();
        Log.d(TAG, "Cleared all cookies");
    }

    /**
     * 清除指定主机的 Cookie
     */
    public void clear(String host) {
        cookieStore.remove(host);
        Log.d(TAG, "Cleared cookies for host: " + host);
    }
}
