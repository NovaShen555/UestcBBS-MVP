package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatMessage {
    @SerializedName("id")
    private int id;

    @SerializedName("message")
    private String message;

    @SerializedName("cooked")
    private String cooked;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("excerpt")
    private String excerpt;

    @SerializedName("user")
    private DiscourseUserResponse.User user;

    @SerializedName("uploads")
    private List<Upload> uploads;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCooked() {
        return cooked;
    }

    public void setCooked(String cooked) {
        this.cooked = cooked;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public DiscourseUserResponse.User getUser() {
        return user;
    }

    public void setUser(DiscourseUserResponse.User user) {
        this.user = user;
    }

    public List<Upload> getUploads() {
        return uploads;
    }

    public void setUploads(List<Upload> uploads) {
        this.uploads = uploads;
    }

    public static class Upload {
        @SerializedName("id")
        private int id;

        @SerializedName("url")
        private String url;

        @SerializedName("original_filename")
        private String originalFilename;

        @SerializedName("extension")
        private String extension;

        @SerializedName("thumbnail")
        private Thumbnail thumbnail;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }

        public void setOriginalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
        }

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
        }
    }

    public static class Thumbnail {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
