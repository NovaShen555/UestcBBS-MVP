package com.novashen.riverside.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * GitHub API Retrofit 工具类
 */
public class GitHubRetrofitUtil {
    private static final String BASE_URL = "https://api.github.com/";
    private static volatile GitHubRetrofitUtil instance;
    private GitHubApiService apiService;

    private GitHubRetrofitUtil() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiService = retrofit.create(GitHubApiService.class);
    }

    public static GitHubRetrofitUtil getInstance() {
        if (instance == null) {
            synchronized (GitHubRetrofitUtil.class) {
                if (instance == null) {
                    instance = new GitHubRetrofitUtil();
                }
            }
        }
        return instance;
    }

    public GitHubApiService getApiService() {
        return apiService;
    }
}
