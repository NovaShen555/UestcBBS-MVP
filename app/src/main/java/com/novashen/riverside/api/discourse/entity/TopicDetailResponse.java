package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Discourse 帖子详情响应
 * 对应 /t/{topic_id}.json 接口
 */
public class TopicDetailResponse {
    public int id;
    public String title;

    @SerializedName("fancy_title")
    public String fancyTitle;

    @SerializedName("posts_count")
    public int postsCount;

    @SerializedName("created_at")
    public String createdAt;

    public int views;

    @SerializedName("reply_count")
    public int replyCount;

    @SerializedName("like_count")
    public int likeCount;

    @SerializedName("last_posted_at")
    public String lastPostedAt;

    public boolean visible;
    public boolean closed;
    public boolean archived;

    @SerializedName("category_id")
    public int categoryId;

    @SerializedName("user_id")
    public int userId;

    @SerializedName("image_url")
    public String imageUrl;

    @SerializedName("post_stream")
    public PostStream postStream;

    public Details details;

    public static class PostStream {
        public List<Post> posts;
        public List<Integer> stream;
    }

    public static class Post {
        public int id;
        public String name;
        public String username;

        @SerializedName("avatar_template")
        public String avatarTemplate;

        @SerializedName("created_at")
        public String createdAt;

        public String cooked;

        @SerializedName("post_number")
        public int postNumber;

        @SerializedName("post_type")
        public int postType;

        @SerializedName("updated_at")
        public String updatedAt;

        @SerializedName("reply_count")
        public int replyCount;

        @SerializedName("reply_to_post_number")
        public Integer replyToPostNumber;

        @SerializedName("quote_count")
        public int quoteCount;

        public int reads;

        @SerializedName("readers_count")
        public int readersCount;

        public double score;

        @SerializedName("topic_id")
        public int topicId;

        @SerializedName("topic_slug")
        public String topicSlug;

        @SerializedName("display_username")
        public String displayUsername;

        @SerializedName("primary_group_name")
        public String primaryGroupName;

        @SerializedName("flair_name")
        public String flairName;

        @SerializedName("flair_url")
        public String flairUrl;

        @SerializedName("flair_bg_color")
        public String flairBgColor;

        @SerializedName("flair_color")
        public String flairColor;

        public int version;

        @SerializedName("can_edit")
        public boolean canEdit;

        @SerializedName("can_delete")
        public boolean canDelete;

        public boolean read;

        @SerializedName("user_title")
        public String userTitle;

        public boolean bookmarked;

        @SerializedName("bookmark_id")
        public Integer bookmarkId;

        @SerializedName("actions_summary")
        public List<ActionSummary> actionsSummary;

        public boolean moderator;
        public boolean admin;
        public boolean staff;

        @SerializedName("user_id")
        public int userId;

        public boolean hidden;

        @SerializedName("trust_level")
        public int trustLevel;

        @SerializedName("deleted_at")
        public String deletedAt;

        @SerializedName("user_deleted")
        public boolean userDeleted;

        @SerializedName("edit_reason")
        public String editReason;

        public boolean wiki;

        public List<Reaction> reactions;

        @SerializedName("current_user_reaction")
        public Reaction currentUserReaction;

        @SerializedName("reaction_users_count")
        public int reactionUsersCount;

        @SerializedName("reply_to_user")
        public ReplyToUser replyToUser;
    }

    public static class ActionSummary {
        public int id;
        public int count;

        @SerializedName("can_act")
        public boolean canAct;
    }

    public static class Reaction {
        public String id;
        public String type;
        public int count;

        @SerializedName("can_undo")
        public boolean canUndo;
    }

    public static class ReplyToUser {
        public int id;
        public String username;
        public String name;

        @SerializedName("avatar_template")
        public String avatarTemplate;
    }

    public static class Details {
        @SerializedName("can_edit")
        public boolean canEdit;

        @SerializedName("notification_level")
        public int notificationLevel;

        @SerializedName("can_create_post")
        public boolean canCreatePost;

        public List<Participant> participants;

        @SerializedName("created_by")
        public CreatedBy createdBy;

        @SerializedName("last_poster")
        public LastPoster lastPoster;
    }

    public static class Participant {
        public int id;
        public String username;
        public String name;

        @SerializedName("avatar_template")
        public String avatarTemplate;

        @SerializedName("post_count")
        public int postCount;

        @SerializedName("primary_group_name")
        public String primaryGroupName;

        @SerializedName("flair_name")
        public String flairName;

        @SerializedName("flair_url")
        public String flairUrl;

        @SerializedName("trust_level")
        public int trustLevel;
    }

    public static class CreatedBy {
        public int id;
        public String username;
        public String name;

        @SerializedName("avatar_template")
        public String avatarTemplate;
    }

    public static class LastPoster {
        public int id;
        public String username;
        public String name;

        @SerializedName("avatar_template")
        public String avatarTemplate;
    }
}
