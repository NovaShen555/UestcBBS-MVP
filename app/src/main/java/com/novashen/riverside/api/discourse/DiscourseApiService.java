package com.novashen.riverside.api.discourse;

import com.novashen.riverside.api.discourse.entity.CsrfTokenResponse;
import com.novashen.riverside.api.discourse.entity.LoginResponse;
import com.novashen.riverside.api.discourse.entity.LatestTopicsResponse;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Discourse API 接口定义
 */
public interface DiscourseApiService {

    /**
     * 获取 CSRF Token
     */
    @GET("session/csrf")
    Observable<CsrfTokenResponse> getCsrfToken();

    /**
     * 登录
     * @param login 用户名
     * @param password 密码
     * @param secondFactorMethod 二次验证方法（默认1）
     * @param timezone 时区
     * @param csrfToken CSRF Token
     */
    @FormUrlEncoded
    @POST("session")
    Observable<Response<LoginResponse>> login(
            @Field("login") String login,
            @Field("password") String password,
            @Field("second_factor_method") int secondFactorMethod,
            @Field("timezone") String timezone,
            @Header("x-csrf-token") String csrfToken
    );

    /**
     * 获取最新帖子列表
     */
    @GET("latest.json")
    Observable<LatestTopicsResponse> getLatestTopics();

    /**
     * 获取当前用户信息
     */
    @GET("session/current.json")
    Observable<Response<ResponseBody>> getCurrentUser();
}
