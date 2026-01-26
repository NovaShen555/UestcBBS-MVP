package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Discourse 帖子列表完整响应
 * 包含用户信息、分组信息和帖子列表
 */
public class TopicListResponse {
    @SerializedName("users")
    private List<User> users;

    @SerializedName("primary_groups")
    private List<Group> primaryGroups;

    @SerializedName("flair_groups")
    private List<FlairGroup> flairGroups;

    @SerializedName("topic_list")
    private TopicList topicList;

    // Getters and Setters
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Group> getPrimaryGroups() {
        return primaryGroups;
    }

    public void setPrimaryGroups(List<Group> primaryGroups) {
        this.primaryGroups = primaryGroups;
    }

    public List<FlairGroup> getFlairGroups() {
        return flairGroups;
    }

    public void setFlairGroups(List<FlairGroup> flairGroups) {
        this.flairGroups = flairGroups;
    }

    public TopicList getTopicList() {
        return topicList;
    }

    public void setTopicList(TopicList topicList) {
        this.topicList = topicList;
    }

    /**
     * 用户信息
     */
    public static class User {
        @SerializedName("id")
        private int id;

        @SerializedName("username")
        private String username;

        @SerializedName("name")
        private String name;

        @SerializedName("avatar_template")
        private String avatarTemplate;

        @SerializedName("primary_group_name")
        private String primaryGroupName;

        @SerializedName("flair_name")
        private String flairName;

        @SerializedName("flair_url")
        private String flairUrl;

        @SerializedName("flair_group_id")
        private Integer flairGroupId;

        @SerializedName("trust_level")
        private int trustLevel;

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

        public String getPrimaryGroupName() {
            return primaryGroupName;
        }

        public void setPrimaryGroupName(String primaryGroupName) {
            this.primaryGroupName = primaryGroupName;
        }

        public String getFlairName() {
            return flairName;
        }

        public void setFlairName(String flairName) {
            this.flairName = flairName;
        }

        public String getFlairUrl() {
            return flairUrl;
        }

        public void setFlairUrl(String flairUrl) {
            this.flairUrl = flairUrl;
        }

        public Integer getFlairGroupId() {
            return flairGroupId;
        }

        public void setFlairGroupId(Integer flairGroupId) {
            this.flairGroupId = flairGroupId;
        }

        public int getTrustLevel() {
            return trustLevel;
        }

        public void setTrustLevel(int trustLevel) {
            this.trustLevel = trustLevel;
        }

        /**
         * 获取头像 URL（指定尺寸）
         */
        public String getAvatarUrl(int size) {
            if (avatarTemplate != null) {
                return "https://river-side.cc" + avatarTemplate.replace("{size}", String.valueOf(size));
            }
            return null;
        }
    }

    /**
     * 用户组信息
     */
    public static class Group {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

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
    }

    /**
     * 徽章组信息
     */
    public static class FlairGroup {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("flair_url")
        private String flairUrl;

        @SerializedName("flair_bg_color")
        private String flairBgColor;

        @SerializedName("flair_color")
        private String flairColor;

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

        public String getFlairUrl() {
            return flairUrl;
        }

        public void setFlairUrl(String flairUrl) {
            this.flairUrl = flairUrl;
        }

        public String getFlairBgColor() {
            return flairBgColor;
        }

        public void setFlairBgColor(String flairBgColor) {
            this.flairBgColor = flairBgColor;
        }

        public String getFlairColor() {
            return flairColor;
        }

        public void setFlairColor(String flairColor) {
            this.flairColor = flairColor;
        }
    }

    /**
     * 帖子列表
     */
    public static class TopicList {
        @SerializedName("can_create_topic")
        private boolean canCreateTopic;

        @SerializedName("more_topics_url")
        private String moreTopicsUrl;

        @SerializedName("per_page")
        private int perPage;

        @SerializedName("top_tags")
        private List<String> topTags;

        @SerializedName("topics")
        private List<Topic> topics;

        public boolean isCanCreateTopic() {
            return canCreateTopic;
        }

        public void setCanCreateTopic(boolean canCreateTopic) {
            this.canCreateTopic = canCreateTopic;
        }

        public String getMoreTopicsUrl() {
            return moreTopicsUrl;
        }

        public void setMoreTopicsUrl(String moreTopicsUrl) {
            this.moreTopicsUrl = moreTopicsUrl;
        }

        public int getPerPage() {
            return perPage;
        }

        public void setPerPage(int perPage) {
            this.perPage = perPage;
        }

        public List<String> getTopTags() {
            return topTags;
        }

        public void setTopTags(List<String> topTags) {
            this.topTags = topTags;
        }

        public List<Topic> getTopics() {
            return topics;
        }

        public void setTopics(List<Topic> topics) {
            this.topics = topics;
        }
    }

    /**
     * 帖子信息
     */
    public static class Topic {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("fancy_title")
        private String fancyTitle;

        @SerializedName("slug")
        private String slug;

        @SerializedName("posts_count")
        private int postsCount;

        @SerializedName("reply_count")
        private int replyCount;

        @SerializedName("highest_post_number")
        private int highestPostNumber;

        @SerializedName("image_url")
        private String imageUrl;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("last_posted_at")
        private String lastPostedAt;

        @SerializedName("bumped")
        private boolean bumped;

        @SerializedName("bumped_at")
        private String bumpedAt;

        @SerializedName("archetype")
        private String archetype;

        @SerializedName("unseen")
        private boolean unseen;

        @SerializedName("pinned")
        private boolean pinned;

        @SerializedName("unpinned")
        private Boolean unpinned;

        @SerializedName("visible")
        private boolean visible;

        @SerializedName("closed")
        private boolean closed;

        @SerializedName("archived")
        private boolean archived;

        @SerializedName("bookmarked")
        private Boolean bookmarked;

        @SerializedName("liked")
        private Boolean liked;

        @SerializedName("tags")
        private List<String> tags;

        @SerializedName("views")
        private int views;

        @SerializedName("like_count")
        private int likeCount;

        @SerializedName("has_summary")
        private boolean hasSummary;

        @SerializedName("last_poster_username")
        private String lastPosterUsername;

        @SerializedName("category_id")
        private int categoryId;

        @SerializedName("op_like_count")
        private int opLikeCount;

        @SerializedName("pinned_globally")
        private boolean pinnedGlobally;

        @SerializedName("featured_link")
        private String featuredLink;

        @SerializedName("is_hot")
        private boolean isHot;

        @SerializedName("has_accepted_answer")
        private boolean hasAcceptedAnswer;

        @SerializedName("can_vote")
        private boolean canVote;

        @SerializedName("posters")
        private List<Poster> posters;

        // Getters and Setters
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

        public String getFancyTitle() {
            return fancyTitle;
        }

        public void setFancyTitle(String fancyTitle) {
            this.fancyTitle = fancyTitle;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public int getPostsCount() {
            return postsCount;
        }

        public void setPostsCount(int postsCount) {
            this.postsCount = postsCount;
        }

        public int getReplyCount() {
            return replyCount;
        }

        public void setReplyCount(int replyCount) {
            this.replyCount = replyCount;
        }

        public int getHighestPostNumber() {
            return highestPostNumber;
        }

        public void setHighestPostNumber(int highestPostNumber) {
            this.highestPostNumber = highestPostNumber;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getLastPostedAt() {
            return lastPostedAt;
        }

        public void setLastPostedAt(String lastPostedAt) {
            this.lastPostedAt = lastPostedAt;
        }

        public boolean isBumped() {
            return bumped;
        }

        public void setBumped(boolean bumped) {
            this.bumped = bumped;
        }

        public String getBumpedAt() {
            return bumpedAt;
        }

        public void setBumpedAt(String bumpedAt) {
            this.bumpedAt = bumpedAt;
        }

        public String getArchetype() {
            return archetype;
        }

        public void setArchetype(String archetype) {
            this.archetype = archetype;
        }

        public boolean isUnseen() {
            return unseen;
        }

        public void setUnseen(boolean unseen) {
            this.unseen = unseen;
        }

        public boolean isPinned() {
            return pinned;
        }

        public void setPinned(boolean pinned) {
            this.pinned = pinned;
        }

        public Boolean getUnpinned() {
            return unpinned;
        }

        public void setUnpinned(Boolean unpinned) {
            this.unpinned = unpinned;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public boolean isClosed() {
            return closed;
        }

        public void setClosed(boolean closed) {
            this.closed = closed;
        }

        public boolean isArchived() {
            return archived;
        }

        public void setArchived(boolean archived) {
            this.archived = archived;
        }

        public Boolean getBookmarked() {
            return bookmarked;
        }

        public void setBookmarked(Boolean bookmarked) {
            this.bookmarked = bookmarked;
        }

        public Boolean getLiked() {
            return liked;
        }

        public void setLiked(Boolean liked) {
            this.liked = liked;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
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

        public boolean isHasSummary() {
            return hasSummary;
        }

        public void setHasSummary(boolean hasSummary) {
            this.hasSummary = hasSummary;
        }

        public String getLastPosterUsername() {
            return lastPosterUsername;
        }

        public void setLastPosterUsername(String lastPosterUsername) {
            this.lastPosterUsername = lastPosterUsername;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public int getOpLikeCount() {
            return opLikeCount;
        }

        public void setOpLikeCount(int opLikeCount) {
            this.opLikeCount = opLikeCount;
        }

        public boolean isPinnedGlobally() {
            return pinnedGlobally;
        }

        public void setPinnedGlobally(boolean pinnedGlobally) {
            this.pinnedGlobally = pinnedGlobally;
        }

        public String getFeaturedLink() {
            return featuredLink;
        }

        public void setFeaturedLink(String featuredLink) {
            this.featuredLink = featuredLink;
        }

        public boolean isHot() {
            return isHot;
        }

        public void setHot(boolean hot) {
            isHot = hot;
        }

        public boolean isHasAcceptedAnswer() {
            return hasAcceptedAnswer;
        }

        public void setHasAcceptedAnswer(boolean hasAcceptedAnswer) {
            this.hasAcceptedAnswer = hasAcceptedAnswer;
        }

        public boolean isCanVote() {
            return canVote;
        }

        public void setCanVote(boolean canVote) {
            this.canVote = canVote;
        }

        public List<Poster> getPosters() {
            return posters;
        }

        public void setPosters(List<Poster> posters) {
            this.posters = posters;
        }
    }

    /**
     * 发帖人信息
     */
    public static class Poster {
        @SerializedName("extras")
        private String extras;

        @SerializedName("description")
        private String description;

        @SerializedName("user_id")
        private int userId;

        @SerializedName("primary_group_id")
        private Integer primaryGroupId;

        @SerializedName("flair_group_id")
        private Integer flairGroupId;

        public String getExtras() {
            return extras;
        }

        public void setExtras(String extras) {
            this.extras = extras;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public Integer getPrimaryGroupId() {
            return primaryGroupId;
        }

        public void setPrimaryGroupId(Integer primaryGroupId) {
            this.primaryGroupId = primaryGroupId;
        }

        public Integer getFlairGroupId() {
            return flairGroupId;
        }

        public void setFlairGroupId(Integer flairGroupId) {
            this.flairGroupId = flairGroupId;
        }
    }
}
