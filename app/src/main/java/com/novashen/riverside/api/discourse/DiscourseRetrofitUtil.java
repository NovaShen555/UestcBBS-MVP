package com.novashen.riverside.api.discourse;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.novashen.riverside.api.discourse.interceptor.DiscourseAuthInterceptor;
import com.novashen.riverside.api.discourse.interceptor.DiscourseCsrfInterceptor;
import com.novashen.riverside.api.discourse.interceptor.SimpleCookieJar;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Discourse Retrofit 工具类
 */
public class DiscourseRetrofitUtil {
    private static final String BASE_URL = "https://river-side.cc/";

    private Retrofit retrofit;
    private DiscourseApiService apiService;
    private SimpleCookieJar cookieJar;
    private DiscourseCsrfInterceptor csrfInterceptor;
    private static volatile DiscourseRetrofitUtil instance;

    public static DiscourseRetrofitUtil getInstance() {
        if (instance == null) {
            synchronized (DiscourseRetrofitUtil.class) {
                if (instance == null) {
                    instance = new DiscourseRetrofitUtil();
                }
            }
        }
        return instance;
    }

    private DiscourseRetrofitUtil() {
        init();
    }

    private void init() {
        // 创建 Cookie 管理器
        cookieJar = new SimpleCookieJar();

        // 创建 CSRF 拦截器
        csrfInterceptor = new DiscourseCsrfInterceptor(cookieJar);

        // 创建认证拦截器（处理 403 自动重新登录）
        DiscourseAuthInterceptor authInterceptor = new DiscourseAuthInterceptor();

        // 创建 X-Requested-With 拦截器（必需，用于绕过 Cloudflare）
        Interceptor xRequestedWithInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request newRequest = originalRequest.newBuilder()
                        .header("X-Requested-With", "XMLHttpRequest")
                        .build();
                return chain.proceed(newRequest);
            }
        };

        // 创建详细的日志拦截器
        Interceptor loggingInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                logDebug("Request: " + request.method() + " " + request.url());
                logDebug("Request Headers: " + request.headers());

                // 记录请求体
                if (request.body() != null) {
                    okio.Buffer buffer = new okio.Buffer();
                    request.body().writeTo(buffer);
                    String bodyString = buffer.readUtf8();
                    logDebug("Request Body: " + bodyString);
                    logDebug("Request Content-Type: " + request.body().contentType());
                }

                long startTime = System.currentTimeMillis();
                Response response = chain.proceed(request);
                long endTime = System.currentTimeMillis();

                logDebug("Response: " + response.code() + " in " + (endTime - startTime) + "ms");

                // 记录响应体（仅用于调试 403 错误）
                if (response.code() == 403) {
                    String responseBody = response.peekBody(1024 * 1024).string();
                    logDebug("Response Body (403): " + responseBody);
                }

                return response;
            }
        };

        // 创建 OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(xRequestedWithInterceptor)
                .addInterceptor(csrfInterceptor)
                .addInterceptor(authInterceptor)  // 添加认证拦截器
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        // 创建 Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiService = retrofit.create(DiscourseApiService.class);
    }

    public DiscourseApiService getApiService() {
        return apiService;
    }

    public SimpleCookieJar getCookieJar() {
        return cookieJar;
    }

    public DiscourseCsrfInterceptor getCsrfInterceptor() {
        return csrfInterceptor;
    }

    /**
     * 清除所有 Cookie 和 CSRF Token
     */
    public void clearSession() {
        cookieJar.clear();
        csrfInterceptor.clearManualCsrfToken();
    }

    /**
     * 日志输出（兼容单元测试）
     */
    private void logDebug(String message) {
        try {
            Log.d("DiscourseAPI", message);
        } catch (RuntimeException e) {
            System.out.println("DiscourseAPI: " + message);
        }
    }
}
