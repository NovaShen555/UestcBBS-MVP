package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Discourse 创建评论响应
 */
public class CreatePostResponse {
    public String action;
    public Post post;
    public boolean success;

    public static class Post {
        public int id;
        public String name;
        public String username;

        @SerializedName("avatar_template")
        public String avatarTemplate;

        @SerializedName("created_at")
        public String createdAt;

        public String cooked;

        @SerializedName("post_number")
        public int postNumber;

        @SerializedName("post_type")
        public int postType;

        @SerializedName("updated_at")
        public String updatedAt;

        @SerializedName("reply_count")
        public int replyCount;

        @SerializedName("reply_to_post_number")
        public Integer replyToPostNumber;

        @SerializedName("quote_count")
        public int quoteCount;

        @SerializedName("topic_id")
        public int topicId;

        @SerializedName("topic_slug")
        public String topicSlug;

        @SerializedName("user_id")
        public int userId;

        public String raw;

        @SerializedName("trust_level")
        public int trustLevel;
    }
}
