package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Discourse 板块列表响应
 * 对应 /categories.json 接口
 */
public class CategoriesResponse {

    @SerializedName("category_list")
    public CategoryList categoryList;

    public static class CategoryList {
        @SerializedName("categories")
        public List<Category> categories;
    }

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

        @SerializedName("subcategory_ids")
        public List<Integer> subcategoryIds;

        @SerializedName("subcategory_count")
        public int subcategoryCount;

        @SerializedName("parent_category_id")
        public Integer parentCategoryId;
    }
}
