package com.novashen.riverside.api.discourse.converter;

import com.novashen.riverside.api.discourse.entity.TopicListResponse;
import com.novashen.riverside.entity.CommonPostBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Discourse 数据转换器
 * 将 Discourse API 返回的数据转换为原有应用的数据格式
 */
public class DiscourseDataConverter {

    private static final String BASE_URL = "https://river-side.cc";

    /**
     * 将 TopicListResponse 转换为 CommonPostBean
     * 用于帖子列表展示
     */
    public static CommonPostBean convertToCommonPostBean(TopicListResponse response) {
        if (response == null || response.getTopicList() == null) {
            return null;
        }

        CommonPostBean commonPostBean = new CommonPostBean();

        // 创建用户ID到用户信息的映射
        Map<Integer, TopicListResponse.User> userMap = createUserMap(response.getUsers());

        // 转换帖子列表
        List<CommonPostBean.ListBean> topicList = new ArrayList<>();
        if (response.getTopicList().getTopics() != null) {
            for (TopicListResponse.Topic discourseTopic : response.getTopicList().getTopics()) {
                CommonPostBean.ListBean topic = convertTopic(discourseTopic, userMap);
                if (topic != null) {
                    topicList.add(topic);
                }
            }
        }

        // 设置基本信息
        commonPostBean.list = topicList;
        commonPostBean.has_next = response.getTopicList().getMoreTopicsUrl() != null ? 1 : 0;
        commonPostBean.rs = 1; // 成功标记
        commonPostBean.page = 1; // 默认第一页

        return commonPostBean;
    }

    /**
     * 创建用户ID到用户信息的映射
     */
    private static Map<Integer, TopicListResponse.User> createUserMap(List<TopicListResponse.User> users) {
        Map<Integer, TopicListResponse.User> userMap = new HashMap<>();
        if (users != null) {
            for (TopicListResponse.User user : users) {
                userMap.put(user.getId(), user);
            }
        }
        return userMap;
    }

    /**
     * 转换单个帖子
     */
    private static CommonPostBean.ListBean convertTopic(
            TopicListResponse.Topic discourseTopic,
            Map<Integer, TopicListResponse.User> userMap) {

        CommonPostBean.ListBean topic = new CommonPostBean.ListBean();

        // 基本信息
        topic.topic_id = discourseTopic.getId();
        topic.title = discourseTopic.getTitle();
        topic.user_nick_name = getOriginalPosterUsername(discourseTopic, userMap);

        // 统计信息
        topic.replies = discourseTopic.getPostsCount();
        topic.hits = discourseTopic.getViews();
        topic.vote = discourseTopic.getLikeCount();

        // 时间信息
        topic.last_reply_date = formatDate(discourseTopic.getLastPostedAt());

        // 用户信息
        TopicListResponse.User originalPoster = getOriginalPoster(discourseTopic, userMap);
        if (originalPoster != null) {
            topic.user_id = originalPoster.getId();
            topic.userAvatar = originalPoster.getAvatarUrl(120); // 使用120x120的头像
        }

        // 板块ID
        topic.board_id = discourseTopic.getCategoryId();

        // 图片URL（如果有）
        if (discourseTopic.getImageUrl() != null) {
            topic.pic_path = discourseTopic.getImageUrl();
        }

        // 状态标记
        topic.top = discourseTopic.isPinned() ? 1 : 0;
        topic.essence = discourseTopic.isHot() ? 1 : 0;
        topic.status = 1; // 默认正常状态

        return topic;
    }

    /**
     * 获取原始发帖人
     */
    private static TopicListResponse.User getOriginalPoster(
            TopicListResponse.Topic topic,
            Map<Integer, TopicListResponse.User> userMap) {

        if (topic.getPosters() != null && !topic.getPosters().isEmpty()) {
            // 第一个 poster 通常是原始发帖人
            TopicListResponse.Poster firstPoster = topic.getPosters().get(0);
            return userMap.get(firstPoster.getUserId());
        }
        return null;
    }

    /**
     * 获取原始发帖人用户名
     */
    private static String getOriginalPosterUsername(
            TopicListResponse.Topic topic,
            Map<Integer, TopicListResponse.User> userMap) {

        TopicListResponse.User user = getOriginalPoster(topic, userMap);
        return user != null ? user.getUsername() : "未知用户";
    }

    /**
     * 格式化日期
     * 将 ISO 8601 格式转换为时间戳字符串
     */
    private static String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }

        try {
            // ISO 8601 格式: 2026-01-25T07:49:18.496Z
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(isoDate);

            // 返回时间戳（毫秒）
            return String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return String.valueOf(System.currentTimeMillis());
        }
    }

    /**
     * 获取用户头像URL
     * @param avatarTemplate 头像模板
     * @param size 尺寸
     */
    public static String getAvatarUrl(String avatarTemplate, int size) {
        if (avatarTemplate == null || avatarTemplate.isEmpty()) {
            return null;
        }
        return BASE_URL + avatarTemplate.replace("{size}", String.valueOf(size));
    }

    /**
     * 获取完整的图片URL
     */
    public static String getFullImageUrl(String relativeUrl) {
        if (relativeUrl == null || relativeUrl.isEmpty()) {
            return null;
        }
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }
        return BASE_URL + relativeUrl;
    }
}
