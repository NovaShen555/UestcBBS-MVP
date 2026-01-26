package com.novashen.riverside.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * GitHub Release 响应实体
 */
public class GitHubReleaseBean {
    @SerializedName("tag_name")
    public String tagName;

    public String name;

    public String body;

    @SerializedName("published_at")
    public String publishedAt;

    @SerializedName("html_url")
    public String htmlUrl;

    public boolean prerelease;

    public boolean draft;

    public List<Asset> assets;

    public static class Asset {
        public String name;

        @SerializedName("browser_download_url")
        public String browserDownloadUrl;

        public long size;

        @SerializedName("content_type")
        public String contentType;
    }
}
