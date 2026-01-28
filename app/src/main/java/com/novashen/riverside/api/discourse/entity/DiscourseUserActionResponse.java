package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Discourse User Actions Response
 * For /user_actions.json
 */
public class DiscourseUserActionResponse {
    @SerializedName("user_actions")
    private List<UserAction> userActions;

    public List<UserAction> getUserActions() {
        return userActions;
    }

    public void setUserActions(List<UserAction> userActions) {
        this.userActions = userActions;
    }

    public static class UserAction {
        @SerializedName("excerpt")
        private String excerpt;

        @SerializedName("action_type")
        private int actionType; // 5 for Reply

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("avatar_template")
        private String avatarTemplate;

        @SerializedName("slug")
        private String slug;

        @SerializedName("topic_id")
        private int topicId;

        @SerializedName("post_number")
        private int postNumber;

        @SerializedName("post_id")
        private int postId;

        @SerializedName("username")
        private String username;

        @SerializedName("name")
        private String name;

        @SerializedName("user_id")
        private int userId;

        @SerializedName("title")
        private String title;

        public String getExcerpt() { return excerpt; }
        public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

        public int getActionType() { return actionType; }
        public void setActionType(int actionType) { this.actionType = actionType; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getAvatarTemplate() { return avatarTemplate; }
        public void setAvatarTemplate(String avatarTemplate) { this.avatarTemplate = avatarTemplate; }

        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }

        public int getTopicId() { return topicId; }
        public void setTopicId(int topicId) { this.topicId = topicId; }

        public int getPostNumber() { return postNumber; }
        public void setPostNumber(int postNumber) { this.postNumber = postNumber; }

        public int getPostId() { return postId; }
        public void setPostId(int postId) { this.postId = postId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getAvatarUrl(int size) {
            if (avatarTemplate == null) {
                return null;
            }
            String url = avatarTemplate.replace("{size}", String.valueOf(size));
            if (!url.startsWith("http")) {
                if (url.startsWith("/")) {
                    return "https://river-side.cc" + url;
                } else {
                    return "https://river-side.cc/" + url;
                }
            }
            return url;
        }
    }
}
