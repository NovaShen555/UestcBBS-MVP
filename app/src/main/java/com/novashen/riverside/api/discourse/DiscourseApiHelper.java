package com.novashen.riverside.api.discourse;

import com.novashen.riverside.api.discourse.converter.DiscourseDataConverter;
import com.novashen.riverside.api.discourse.entity.DiscourseUserActionResponse;
import com.novashen.riverside.api.discourse.entity.DiscourseUserBookmarksResponse;
import com.novashen.riverside.api.discourse.entity.TopicListResponse;
import com.novashen.riverside.entity.CommonPostBean;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Discourse API 帮助类
 * 提供便捷的方法来获取和转换 Discourse 数据
 */
public class DiscourseApiHelper {

    private DiscourseApiService apiService;

    public DiscourseApiHelper(DiscourseApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * 获取最新回复的帖子列表（转换为 CommonPostBean 格式）
     */
    public Observable<CommonPostBean> getLatestTopicsAsCommonPost() {
        return apiService.getLatestTopics()
                .map(new Function<TopicListResponse, CommonPostBean>() {
                    @Override
                    public CommonPostBean apply(TopicListResponse response) throws Exception {
                        return DiscourseDataConverter.convertToCommonPostBean(response);
                    }
                });
    }

    /**
     * 获取最新创建的帖子列表（转换为 CommonPostBean 格式）
     */
    public Observable<CommonPostBean> getNewTopicsAsCommonPost() {
        return apiService.getNewTopics()
                .map(new Function<TopicListResponse, CommonPostBean>() {
                    @Override
                    public CommonPostBean apply(TopicListResponse response) throws Exception {
                        return DiscourseDataConverter.convertToCommonPostBean(response);
                    }
                });
    }

    /**
     * 获取最新回复的帖子列表（原始 Discourse 格式）
     */
    public Observable<TopicListResponse> getLatestTopics() {
        return apiService.getLatestTopics();
    }

    /**
     * 获取最新创建的帖子列表（原始 Discourse 格式）
     */
    public Observable<TopicListResponse> getNewTopics() {
        return apiService.getNewTopics();
    }

    public Observable<CommonPostBean> getUserTopicsAsCommonPost(String username) {
        return apiService.getUserTopics(username)
                .map(new Function<TopicListResponse, CommonPostBean>() {
                    @Override
                    public CommonPostBean apply(TopicListResponse response) throws Exception {
                        return DiscourseDataConverter.convertToCommonPostBean(response);
                    }
                });
    }

    public Observable<CommonPostBean> getUserActionsAsCommonPost(String username, int offset) {
        return getUserActionsAsCommonPost(username, offset, 5);
    }

    public Observable<CommonPostBean> getUserActionsAsCommonPost(String username, int offset, int filter) {
        return apiService.getUserActions(offset, username, filter)
                .map(new Function<DiscourseUserActionResponse, CommonPostBean>() {
                    @Override
                    public CommonPostBean apply(DiscourseUserActionResponse response) throws Exception {
                        return DiscourseDataConverter.convertUserActionsToCommonPostBean(response);
                    }
                });
    }

    public Observable<CommonPostBean> getUserLikesAsCommonPost(String username, int offset) {
        return getUserActionsAsCommonPost(username, offset, 1);
    }

    public Observable<CommonPostBean> getUserBookmarksAsCommonPost(String username, int page) {
        return apiService.getUserBookmarks(username, page)
                .map(new Function<DiscourseUserBookmarksResponse, CommonPostBean>() {
                    @Override
                    public CommonPostBean apply(DiscourseUserBookmarksResponse response) throws Exception {
                        return DiscourseDataConverter.convertBookmarksToCommonPostBean(response);
                    }
                });
    }
}
