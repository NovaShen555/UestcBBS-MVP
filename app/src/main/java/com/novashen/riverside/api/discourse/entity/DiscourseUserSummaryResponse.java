package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Discourse用户摘要信息响应
 * 对应 /u/{username}/summary.json 接口
 */
public class DiscourseUserSummaryResponse {
    @SerializedName("user_summary")
    private UserSummary userSummary;

    public UserSummary getUserSummary() {
        return userSummary;
    }

    public void setUserSummary(UserSummary userSummary) {
        this.userSummary = userSummary;
    }

    public static class UserSummary {
        @SerializedName("likes_given")
        private int likesGiven;

        @SerializedName("likes_received")
        private int likesReceived;

        @SerializedName("topics_entered")
        private int topicsEntered;

        @SerializedName("posts_read_count")
        private int postsReadCount;

        @SerializedName("days_visited")
        private int daysVisited;

        @SerializedName("topic_count")
        private int topicCount;

        @SerializedName("post_count")
        private int postCount;

        @SerializedName("time_read")
        private int timeRead;

        @SerializedName("recent_time_read")
        private int recentTimeRead;

        public int getLikesGiven() {
            return likesGiven;
        }

        public void setLikesGiven(int likesGiven) {
            this.likesGiven = likesGiven;
        }

        public int getLikesReceived() {
            return likesReceived;
        }

        public void setLikesReceived(int likesReceived) {
            this.likesReceived = likesReceived;
        }

        public int getTopicsEntered() {
            return topicsEntered;
        }

        public void setTopicsEntered(int topicsEntered) {
            this.topicsEntered = topicsEntered;
        }

        public int getPostsReadCount() {
            return postsReadCount;
        }

        public void setPostsReadCount(int postsReadCount) {
            this.postsReadCount = postsReadCount;
        }

        public int getDaysVisited() {
            return daysVisited;
        }

        public void setDaysVisited(int daysVisited) {
            this.daysVisited = daysVisited;
        }

        public int getTopicCount() {
            return topicCount;
        }

        public void setTopicCount(int topicCount) {
            this.topicCount = topicCount;
        }

        public int getPostCount() {
            return postCount;
        }

        public void setPostCount(int postCount) {
            this.postCount = postCount;
        }

        public int getTimeRead() {
            return timeRead;
        }

        public void setTimeRead(int timeRead) {
            this.timeRead = timeRead;
        }

        public int getRecentTimeRead() {
            return recentTimeRead;
        }

        public void setRecentTimeRead(int recentTimeRead) {
            this.recentTimeRead = recentTimeRead;
        }
    }
}
