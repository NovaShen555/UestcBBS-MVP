package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Discourse User Bookmarks Response
 * For /u/{username}/bookmarks.json
 */
public class DiscourseUserBookmarksResponse {
    @SerializedName("user_bookmark_list")
    private UserBookmarkList userBookmarkList;

    public UserBookmarkList getUserBookmarkList() {
        return userBookmarkList;
    }

    public void setUserBookmarkList(UserBookmarkList userBookmarkList) {
        this.userBookmarkList = userBookmarkList;
    }

    public static class UserBookmarkList {
        @SerializedName("bookmarks")
        private List<Bookmark> bookmarks;

        public List<Bookmark> getBookmarks() {
            return bookmarks;
        }

        public void setBookmarks(List<Bookmark> bookmarks) {
            this.bookmarks = bookmarks;
        }
    }

    public static class Bookmark {
        @SerializedName("id")
        private int id;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("title")
        private String title;

        @SerializedName("excerpt")
        private String excerpt;

        @SerializedName("bookmarkable_id")
        private int bookmarkableId;

        @SerializedName("bookmarkable_type")
        private String bookmarkableType;

        @SerializedName("bookmarkable_url")
        private String bookmarkableUrl;

        @SerializedName("topic_id")
        private int topicId;

        @SerializedName("linked_post_number")
        private int linkedPostNumber;

        @SerializedName("category_id")
        private int categoryId;

        @SerializedName("bumped_at")
        private String bumpedAt;

        @SerializedName("slug")
        private String slug;

        @SerializedName("tags")
        private List<String> tags;

        @SerializedName("user")
        private User user;

        public int getId() {
            return id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getTitle() {
            return title;
        }

        public String getExcerpt() {
            return excerpt;
        }

        public int getBookmarkableId() {
            return bookmarkableId;
        }

        public String getBookmarkableType() {
            return bookmarkableType;
        }

        public String getBookmarkableUrl() {
            return bookmarkableUrl;
        }

        public int getTopicId() {
            return topicId;
        }

        public int getLinkedPostNumber() {
            return linkedPostNumber;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public String getBumpedAt() {
            return bumpedAt;
        }

        public String getSlug() {
            return slug;
        }

        public List<String> getTags() {
            return tags;
        }

        public User getUser() {
            return user;
        }
    }

    public static class User {
        @SerializedName("id")
        private int id;

        @SerializedName("username")
        private String username;

        @SerializedName("name")
        private String name;

        @SerializedName("avatar_template")
        private String avatarTemplate;

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getName() {
            return name;
        }

        public String getAvatarTemplate() {
            return avatarTemplate;
        }
    }
}
