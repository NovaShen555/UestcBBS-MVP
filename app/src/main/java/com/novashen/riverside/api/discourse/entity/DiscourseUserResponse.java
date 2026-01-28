package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Discourse用户信息响应
 * 对应 /u/{username}.json 接口
 */
public class DiscourseUserResponse {
    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

        @SerializedName("trust_level")
        private int trustLevel;

        @SerializedName("title")
        private String title;

        @SerializedName("profile_view_count")
        private int profileViewCount;

        @SerializedName("total_followers")
        private int totalFollowers;

        @SerializedName("total_following")
        private int totalFollowing;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("last_seen_at")
        private String lastSeenAt;

        @SerializedName("location")
        private String location;

        @SerializedName("website")
        private String website;

        @SerializedName("bio_raw")
        private String bioRaw;

        @SerializedName("card_background_upload_url")
        private String cardBackgroundUploadUrl;

        @SerializedName("user_badges")
        private List<UserBadge> userBadges;

        // Getters and Setters
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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getProfileViewCount() {
            return profileViewCount;
        }

        public void setProfileViewCount(int profileViewCount) {
            this.profileViewCount = profileViewCount;
        }

        public int getTotalFollowers() {
            return totalFollowers;
        }

        public void setTotalFollowers(int totalFollowers) {
            this.totalFollowers = totalFollowers;
        }

        public int getTotalFollowing() {
            return totalFollowing;
        }

        public void setTotalFollowing(int totalFollowing) {
            this.totalFollowing = totalFollowing;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getLastSeenAt() {
            return lastSeenAt;
        }

        public void setLastSeenAt(String lastSeenAt) {
            this.lastSeenAt = lastSeenAt;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getBioRaw() {
            return bioRaw;
        }

        public void setBioRaw(String bioRaw) {
            this.bioRaw = bioRaw;
        }

        public String getCardBackgroundUploadUrl() {
            return cardBackgroundUploadUrl;
        }

        public void setCardBackgroundUploadUrl(String cardBackgroundUploadUrl) {
            this.cardBackgroundUploadUrl = cardBackgroundUploadUrl;
        }

        public List<UserBadge> getUserBadges() {
            return userBadges;
        }

        public void setUserBadges(List<UserBadge> userBadges) {
            this.userBadges = userBadges;
        }

        /**
         * 获取完整的头像URL
         * @param size 头像尺寸
         * @return 完整的头像URL
         */
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

    public static class UserBadge {
        @SerializedName("id")
        private int id;

        @SerializedName("granted_at")
        private String grantedAt;

        @SerializedName("badge_id")
        private int badgeId;

        @SerializedName("badge")
        private Badge badge;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGrantedAt() {
            return grantedAt;
        }

        public void setGrantedAt(String grantedAt) {
            this.grantedAt = grantedAt;
        }

        public int getBadgeId() {
            return badgeId;
        }

        public void setBadgeId(int badgeId) {
            this.badgeId = badgeId;
        }

        public Badge getBadge() {
            return badge;
        }

        public void setBadge(Badge badge) {
            this.badge = badge;
        }
    }

    public static class Badge {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        @SerializedName("image_url")
        private String imageUrl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
