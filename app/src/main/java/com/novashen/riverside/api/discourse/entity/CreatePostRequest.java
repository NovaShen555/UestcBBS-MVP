package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 发表帖子/评论请求体
 */
public class CreatePostRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("raw")
    private String raw;

    @SerializedName("topic_id")
    private Integer topicId;

    @SerializedName("category")
    private Integer category;

    @SerializedName("archetype")
    private String archetype;

    @SerializedName("nested_post")
    private Boolean nestedPost;

    @SerializedName("reply_to_post_number")
    private Integer replyToPostNumber;

    @SerializedName("unlist_topic")
    private Boolean unlistTopic;

    @SerializedName("is_warning")
    private Boolean isWarning;

    /**
     * 创建新帖子的构造函数
     */
    public CreatePostRequest(String title, String raw, int category) {
        this.title = title;
        this.raw = raw;
        this.category = category;
        this.archetype = "regular";
    }

    /**
     * 创建评论的构造函数
     */
    public CreatePostRequest(String raw, int topicId) {
        this.raw = raw;
        this.topicId = topicId;
        this.archetype = "regular";
        this.nestedPost = true;
        this.unlistTopic = false;
        this.isWarning = false;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public void setReplyToPostNumber(Integer replyToPostNumber) {
        this.replyToPostNumber = replyToPostNumber;
    }

    public String getRaw() {
        return raw;
    }

    public int getTopicId() {
        return topicId;
    }

    public Integer getCategory() {
        return category;
    }

    public String getArchetype() {
        return archetype;
    }

    public boolean isNestedPost() {
        return nestedPost;
    }

    public Integer getReplyToPostNumber() {
        return replyToPostNumber;
    }

    public boolean isUnlistTopic() {
        return unlistTopic;
    }

    public boolean isWarning() {
        return isWarning;
    }
}
