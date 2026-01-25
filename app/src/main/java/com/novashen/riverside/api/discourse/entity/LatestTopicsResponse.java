package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 最新帖子列表响应
 */
public class LatestTopicsResponse {
    @SerializedName("topic_list")
    private TopicList topicList;

    public TopicList getTopicList() {
        return topicList;
    }

    public void setTopicList(TopicList topicList) {
        this.topicList = topicList;
    }

    public static class TopicList {
        @SerializedName("topics")
        private List<Topic> topics;

        public List<Topic> getTopics() {
            return topics;
        }

        public void setTopics(List<Topic> topics) {
            this.topics = topics;
        }
    }

    public static class Topic {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("posts_count")
        private int postsCount;

        @SerializedName("views")
        private int views;

        @SerializedName("like_count")
        private int likeCount;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPostsCount() {
            return postsCount;
        }

        public void setPostsCount(int postsCount) {
            this.postsCount = postsCount;
        }

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }

        @Override
        public String toString() {
            return "Topic{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", postsCount=" + postsCount +
                    ", views=" + views +
                    ", likeCount=" + likeCount +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LatestTopicsResponse{" +
                "topicList=" + topicList +
                '}';
    }
}
