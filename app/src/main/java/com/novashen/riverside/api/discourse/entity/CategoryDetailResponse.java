package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Discourse 板块详情响应
 * 对应 /c/{id}/show.json 接口
 */
public class CategoryDetailResponse {

    @SerializedName("category")
    public Category category;

    public static class Category {
        @SerializedName("id")
        public int id;

        @SerializedName("name")
        public String name;

        @SerializedName("color")
        public String color;

        @SerializedName("text_color")
        public String textColor;

        @SerializedName("slug")
        public String slug;

        @SerializedName("topic_count")
        public int topicCount;

        @SerializedName("post_count")
        public int postCount;

        @SerializedName("description")
        public String description;

        @SerializedName("description_text")
        public String descriptionText;

        @SerializedName("parent_category_id")
        public Integer parentCategoryId;
    }
}
