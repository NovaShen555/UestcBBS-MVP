package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Discourse 用户关注/粉丝列表项
 * 对应 /u/{username}/follow/followers 与 /u/{username}/follow/following.json
 */
public class DiscourseFollowUser {

    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("name")
    private String name;

    @SerializedName("avatar_template")
    private String avatarTemplate;

    // 需要额外请求用户详情获取
    private String lastSeenAt;

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

    public String getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(String lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
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
