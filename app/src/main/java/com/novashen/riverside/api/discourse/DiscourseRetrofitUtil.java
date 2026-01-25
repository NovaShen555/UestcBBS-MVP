package com.novashen.riverside.api.discourse;

import com.google.gson.GsonBuilder;
import com.novashen.riverside.api.discourse.interceptor.DiscourseCsrfInterceptor;
import com.novashen.riverside.api.discourse.interceptor.SimpleCookieJar;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

        // 创建日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 创建 OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(csrfInterceptor)
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
}
