package com.novashen.riverside.api.discourse;

import com.novashen.riverside.api.discourse.entity.CategoriesResponse;
import com.novashen.riverside.api.discourse.entity.CategoryDetailResponse;
import com.novashen.riverside.api.discourse.entity.CreatePostRequest;
import com.novashen.riverside.api.discourse.entity.CreatePostResponse;
import com.novashen.riverside.api.discourse.entity.CsrfTokenResponse;
import com.novashen.riverside.api.discourse.entity.DiscourseUserResponse;
import com.novashen.riverside.api.discourse.entity.DiscourseUserSummaryResponse;
import com.novashen.riverside.api.discourse.entity.DiscourseUserActionResponse;
import com.novashen.riverside.api.discourse.entity.LoginResponse;
import com.novashen.riverside.api.discourse.entity.TopicDetailResponse;
import com.novashen.riverside.api.discourse.entity.TopicListResponse;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
     * 获取 CSRF Token（原始响应）
     */
    @GET("session/csrf")
    Observable<okhttp3.ResponseBody> getCsrfTokenRaw();

    /**
     * 登录
     * @param login 用户名
     * @param password 密码
     * @param secondFactorMethod 二次验证方法（默认1）
     * @param timezone 时区
     */
    @FormUrlEncoded
    @POST("session")
    Observable<Response<LoginResponse>> login(
            @Field("login") String login,
            @Field("password") String password,
            @Field("second_factor_method") int secondFactorMethod,
            @Field("timezone") String timezone
    );

    /**
     * 获取最新回复的帖子列表
     */
    @GET("latest.json")
    Observable<TopicListResponse> getLatestTopics();

    /**
     * 获取最新创建的帖子列表
     */
    @GET("new.json")
    Observable<TopicListResponse> getNewTopics();

    /**
     * 获取当前用户信息
     */
    @GET("session/current.json")
    Observable<Response<ResponseBody>> getCurrentUser();

    /**
     * 获取帖子详情
     * @param topicId 帖子ID
     */
    @GET("t/{topic_id}.json")
    Observable<TopicDetailResponse> getTopicDetail(@Path("topic_id") int topicId);

    /**
     * 发表评论
     * @param body 请求体（JSON 格式）
     */
    @POST("posts")
    Observable<Response<CreatePostResponse>> createPost(
            @retrofit2.http.Body CreatePostRequest body
    );

    /**
     * 获取所有板块分类
     */
    @GET("categories.json")
    Observable<CategoriesResponse> getCategories();

    /**
     * 获取板块详情
     * @param categoryId 板块ID
     */
    @GET("c/{category_id}/show.json")
    Observable<CategoryDetailResponse> getCategoryDetail(@Path("category_id") int categoryId);

    /**
     * 获取用户信息
     * @param username 用户名
     */
    @GET("u/{username}.json")
    Observable<DiscourseUserResponse> getUserInfo(@Path("username") String username);

    /**
     * 获取用户摘要信息
     * @param username 用户名
     */
    @GET("u/{username}/summary.json")
    Observable<DiscourseUserSummaryResponse> getUserSummary(@Path("username") String username);

    @GET("topics/created-by/{username}.json")
    Observable<TopicListResponse> getUserTopics(@Path("username") String username);

    @GET("user_actions.json")
    Observable<DiscourseUserActionResponse> getUserActions(
        @retrofit2.http.Query("offset") int offset,
        @retrofit2.http.Query("username") String username,
        @retrofit2.http.Query("filter") int filter
    );
}
