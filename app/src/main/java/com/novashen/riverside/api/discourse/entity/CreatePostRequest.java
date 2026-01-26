package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 发表评论请求体
 */
public class CreatePostRequest {
    @SerializedName("raw")
    private String raw;

    @SerializedName("topic_id")
    private int topicId;

    @SerializedName("category")
    private Integer category;

    @SerializedName("archetype")
    private String archetype;

    @SerializedName("nested_post")
    private boolean nestedPost;

    @SerializedName("reply_to_post_number")
    private Integer replyToPostNumber;

    @SerializedName("unlist_topic")
    private boolean unlistTopic;

    @SerializedName("is_warning")
    private boolean isWarning;

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
