package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 登录响应
 */
public class LoginResponse {
    @SerializedName("user")
    private User user;

    @SerializedName("user_badges")
    private List<UserBadge> userBadges;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserBadge> getUserBadges() {
        return userBadges;
    }

    public void setUserBadges(List<UserBadge> userBadges) {
        this.userBadges = userBadges;
    }

    public static class User {
        @SerializedName("id")
        private int id;

        @SerializedName("username")
        private String username;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("avatar_template")
        private String avatarTemplate;

        @SerializedName("trust_level")
        private int trustLevel;

        @SerializedName("moderator")
        private boolean moderator;

        @SerializedName("admin")
        private boolean admin;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAvatarTemplate() {
            return avatarTemplate;
        }

        public void setAvatarTemplate(String avatarTemplate) {
            this.avatarTemplate = avatarTemplate;
        }

        public int getTrustLevel() {
            return trustLevel;
        }

        public void setTrustLevel(int trustLevel) {
            this.trustLevel = trustLevel;
        }

        public boolean isModerator() {
            return moderator;
        }

        public void setModerator(boolean moderator) {
            this.moderator = moderator;
        }

        public boolean isAdmin() {
            return admin;
        }

        public void setAdmin(boolean admin) {
            this.admin = admin;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", trustLevel=" + trustLevel +
                    ", moderator=" + moderator +
                    ", admin=" + admin +
                    '}';
        }
    }

    public static class UserBadge {
        @SerializedName("id")
        private int id;

        @SerializedName("badge_id")
        private int badgeId;

        @SerializedName("user_id")
        private int userId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getBadgeId() {
            return badgeId;
        }

        public void setBadgeId(int badgeId) {
            this.badgeId = badgeId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "user=" + user +
                ", userBadges=" + userBadges +
                '}';
    }
}
