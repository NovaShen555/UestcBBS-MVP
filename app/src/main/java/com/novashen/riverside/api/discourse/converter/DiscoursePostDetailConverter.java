package com.novashen.riverside.api.discourse.converter;

import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.api.discourse.entity.TopicDetailResponse;
import com.novashen.riverside.entity.PostDetailBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Discourse 帖子详情数据转换器
 * 将 TopicDetailResponse 转换为 PostDetailBean
 */
public class DiscoursePostDetailConverter {

    private static final String DISCOURSE_BASE_URL = "https://river-side.cc";
    private static final String AVATAR_SIZE = "120";

    /**
     * 转换帖子详情
     */
    public static PostDetailBean convert(TopicDetailResponse response) {
        PostDetailBean bean = new PostDetailBean();

        // 设置基本响应信息
        bean.rs = ApiConstant.Code.SUCCESS_CODE;
        bean.errcode = "0";
        bean.page = 1;
        bean.has_next = 0; // Discourse 一次返回所有回复
        bean.total_num = response.postsCount;

        // 创建用户信息映射表
        Map<Integer, TopicDetailResponse.Participant> userMap = new HashMap<>();
        if (response.details != null && response.details.participants != null) {
            for (TopicDetailResponse.Participant participant : response.details.participants) {
                userMap.put(participant.id, participant);
            }
        }

        // 转换主题信息
        bean.topic = convertTopic(response, userMap);

        // 转换回复列表
        bean.list = convertPosts(response.postStream.posts, userMap);

        return bean;
    }

    /**
     * 转换主题信息
     */
    private static PostDetailBean.TopicBean convertTopic(TopicDetailResponse response, Map<Integer, TopicDetailResponse.Participant> userMap) {
        PostDetailBean.TopicBean topic = new PostDetailBean.TopicBean();

        // 获取第一个帖子（主题帖）
        if (response.postStream.posts != null && !response.postStream.posts.isEmpty()) {
            TopicDetailResponse.Post firstPost = response.postStream.posts.get(0);

            topic.topic_id = response.id;
            topic.title = response.title;
            topic.user_id = firstPost.userId;
            topic.user_nick_name = firstPost.username;
            topic.replies = response.replyCount;
            topic.hits = response.views;

            // 使用 created_at 时间
            logDebug("Converting topic create_date from: " + firstPost.createdAt);
            topic.create_date = formatDateToTimestamp(firstPost.createdAt);
            logDebug("Topic create_date result: " + topic.create_date);

            // 从 userMap 获取用户信息，如果没有则使用 post 中的信息
            TopicDetailResponse.Participant user = userMap.get(firstPost.userId);
            if (user != null) {
                topic.icon = getAvatarUrl(user.avatarTemplate);
                topic.userTitle = user.name != null ? user.name : "";
                topic.level = user.trustLevel;
            } else {
                topic.icon = getAvatarUrl(firstPost.avatarTemplate);
                topic.userTitle = firstPost.userTitle != null ? firstPost.userTitle : "";
                topic.level = firstPost.trustLevel;
            }

            topic.userColor = "";
            topic.gender = 0; // Discourse 不提供性别信息
            topic.is_favor = 0;
            topic.favoriteNum = 0;
            topic.isFollow = 0;
            topic.vote = response.likeCount;

            // 解析帖子内容
            topic.content = parseContent(firstPost.cooked);
        }

        return topic;
    }

    /**
     * 转换回复列表
     */
    private static List<PostDetailBean.ListBean> convertPosts(List<TopicDetailResponse.Post> posts, Map<Integer, TopicDetailResponse.Participant> userMap) {
        List<PostDetailBean.ListBean> list = new ArrayList<>();

        if (posts == null || posts.isEmpty()) {
            return list;
        }

        // 创建 postNumber 到 Post 的映射，用于查找被引用的帖子
        Map<Integer, TopicDetailResponse.Post> postNumberMap = new HashMap<>();
        for (TopicDetailResponse.Post post : posts) {
            postNumberMap.put(post.postNumber, post);
        }

        // 跳过第一个帖子（主题帖），从第二个开始是回复
        for (int i = 1; i < posts.size(); i++) {
            TopicDetailResponse.Post post = posts.get(i);
            PostDetailBean.ListBean reply = new PostDetailBean.ListBean();

            reply.reply_id = post.userId;
            reply.reply_name = post.username;
            reply.reply_posts_id = post.id;
            reply.position = post.postNumber;

            // 使用 created_at 时间
            logDebug("Converting reply posts_date from: " + post.createdAt);
            reply.posts_date = formatDateToTimestamp(post.createdAt);
            logDebug("Reply posts_date result: " + reply.posts_date);

            // 从 userMap 获取用户信息
            TopicDetailResponse.Participant user = userMap.get(post.userId);
            if (user != null) {
                reply.icon = getAvatarUrl(user.avatarTemplate);
                reply.level = user.trustLevel;
                reply.userTitle = user.name != null ? user.name : "";
            } else {
                reply.icon = getAvatarUrl(post.avatarTemplate);
                reply.level = post.trustLevel;
                reply.userTitle = post.userTitle != null ? post.userTitle : "";
            }

            reply.userColor = "";
            reply.gender = 0;
            reply.status = 0;
            reply.reply_status = 0;

            // 处理引用
            if (post.replyToPostNumber != null && post.replyToPostNumber > 0) {
                reply.is_quote = 1;

                // 从 postNumberMap 中查找被引用的帖子
                TopicDetailResponse.Post quotedPost = postNumberMap.get(post.replyToPostNumber);
                if (quotedPost != null) {
                    // 设置被引用帖子的信息
                    reply.quote_pid = String.valueOf(quotedPost.id);
                    reply.quote_user_name = quotedPost.username;
                    reply.quote_time = formatDateToTimestamp(quotedPost.createdAt);

                    // 提取被引用帖子的纯文本内容（去除 HTML 标签）
                    String quotedContent = extractTextFromHtml(quotedPost.cooked);
                    reply.quote_content = quotedContent;
                    reply.quote_content_bare = quotedContent;
                } else if (post.replyToUser != null) {
                    // 如果找不到被引用的帖子，至少设置用户名
                    reply.quote_user_name = post.replyToUser.username;
                    reply.quote_content = "";
                    reply.quote_content_bare = "";
                }
            } else {
                reply.is_quote = 0;
            }

            // 解析回复内容
            reply.reply_content = parseReplyContent(post.cooked);

            // 点赞信息
            reply.isSupported = false;
            reply.supportedCount = 0;
            if (post.actionsSummary != null) {
                for (TopicDetailResponse.ActionSummary action : post.actionsSummary) {
                    if (action.id == 2) { // 2 表示点赞
                        reply.supportedCount = action.count;
                        break;
                    }
                }
            }

            list.add(reply);
        }

        return list;
    }

    /**
     * 解析主题内容
     */
    private static List<PostDetailBean.TopicBean.ContentBean> parseContent(String html) {
        List<PostDetailBean.TopicBean.ContentBean> contentList = new ArrayList<>();

        if (html == null || html.isEmpty()) {
            return contentList;
        }

        try {
            Document doc = Jsoup.parse(html);
            Elements elements = doc.body().children();

            for (Element element : elements) {
                // 处理图片
                Elements images = element.select("img");
                for (Element img : images) {
                    String src = img.attr("src");
                    if (src != null && !src.isEmpty()) {
                        PostDetailBean.TopicBean.ContentBean content = new PostDetailBean.TopicBean.ContentBean();
                        content.type = 1; // 图片类型
                        content.infor = src.startsWith("http") ? src : DISCOURSE_BASE_URL + src;
                        content.originalInfo = content.infor;
                        contentList.add(content);
                    }
                }

                // 处理文本
                String text = element.text();
                if (text != null && !text.trim().isEmpty()) {
                    PostDetailBean.TopicBean.ContentBean content = new PostDetailBean.TopicBean.ContentBean();
                    content.type = 0; // 文本类型
                    content.infor = text;
                    content.originalInfo = text;
                    contentList.add(content);
                }
            }

            // 如果没有解析到任何内容，添加原始 HTML 作为文本
            if (contentList.isEmpty()) {
                PostDetailBean.TopicBean.ContentBean content = new PostDetailBean.TopicBean.ContentBean();
                content.type = 0;
                content.infor = doc.text();
                content.originalInfo = content.infor;
                contentList.add(content);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 解析失败时，返回纯文本
            PostDetailBean.TopicBean.ContentBean content = new PostDetailBean.TopicBean.ContentBean();
            content.type = 0;
            content.infor = Jsoup.parse(html).text();
            content.originalInfo = content.infor;
            contentList.add(content);
        }

        return contentList;
    }

    /**
     * 解析回复内容
     * 改为保留 HTML 格式，让 TextView 使用 Html.fromHtml() 渲染
     * 这样可以保持图片和文字的原始顺序，实现图文混排
     */
    private static List<PostDetailBean.ListBean.ReplyContentBean> parseReplyContent(String html) {
        List<PostDetailBean.ListBean.ReplyContentBean> contentList = new ArrayList<>();

        if (html == null || html.isEmpty()) {
            return contentList;
        }

        try {
            Document doc = Jsoup.parse(html);

            // 处理图片：添加尺寸信息
            Elements images = doc.select("img");
            for (Element img : images) {
                String src = img.attr("src");
                if (src != null && !src.isEmpty()) {
                    // 确保图片 URL 是完整的
                    if (!src.startsWith("http")) {
                        src = DISCOURSE_BASE_URL + src;
                        img.attr("src", src);
                    }

                    // 获取图片的原始尺寸（如果有）
                    String width = img.attr("width");
                    String height = img.attr("height");

                    // 如果没有尺寸信息，或者尺寸很小（可能是表情包），添加标记
                    if (width.isEmpty() || height.isEmpty()) {
                        // 没有尺寸信息，保持原样
                    } else {
                        int w = Integer.parseInt(width);
                        int h = Integer.parseInt(height);

                        // 如果是小图（表情包），添加 class 标记
                        if (w <= 100 && h <= 100) {
                            img.addClass("emoji");
                        }
                    }
                }
            }

            // 移除引用块（Discourse 的 aside.quote）
            Elements quotes = doc.select("aside.quote");
            quotes.remove();

            // 获取处理后的 HTML
            String processedHtml = doc.body().html();

            // 创建一个文本内容项，包含完整的 HTML
            PostDetailBean.ListBean.ReplyContentBean content = new PostDetailBean.ListBean.ReplyContentBean();
            content.type = 0; // 文本类型（但包含 HTML）
            content.infor = processedHtml;
            content.originalInfo = processedHtml;
            contentList.add(content);

        } catch (Exception e) {
            e.printStackTrace();
            // 解析失败时，返回纯文本
            PostDetailBean.ListBean.ReplyContentBean content = new PostDetailBean.ListBean.ReplyContentBean();
            content.type = 0;
            content.infor = Jsoup.parse(html).text();
            content.originalInfo = content.infor;
            contentList.add(content);
        }

        return contentList;
    }

    /**
     * 获取头像 URL
     */
    private static String getAvatarUrl(String avatarTemplate) {
        if (avatarTemplate == null || avatarTemplate.isEmpty()) {
            return "";
        }

        String url = avatarTemplate.replace("{size}", AVATAR_SIZE);
        return url.startsWith("http") ? url : DISCOURSE_BASE_URL + url;
    }

    /**
     * 格式化日期
     * 将 ISO 8601 格式转换为时间戳字符串
     */
    private static String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(isoDate);

            if (date != null) {
                return String.valueOf(date.getTime() / 1000); // 转换为秒级时间戳
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 格式化日期为时间戳
     * 将 ISO 8601 格式转换为时间戳字符串（毫秒级，因为 TimeUtil.formatTime 需要毫秒）
     */
    private static String formatDateToTimestamp(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            logWarn("Empty date string");
            return "0";
        }

        logDebug("Parsing date: " + isoDate);

        try {
            // Discourse 可能返回多种格式，尝试不同的格式
            SimpleDateFormat[] formats = {
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
            };

            for (int i = 0; i < formats.length; i++) {
                SimpleDateFormat format = formats[i];
                try {
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = format.parse(isoDate);

                    if (date != null) {
                        long timestampMs = date.getTime(); // 毫秒级时间戳
                        logDebug("Format " + i + " succeeded. Date object: " + date.toString());
                        logDebug("Timestamp (ms): " + timestampMs);
                        // 返回毫秒级时间戳（TimeUtil.formatTime 需要毫秒）
                        return String.valueOf(timestampMs);
                    }
                } catch (Exception e) {
                    logDebug("Format " + i + " failed: " + e.getMessage());
                    // 尝试下一个格式
                    continue;
                }
            }

            logError("Failed to parse date with all formats: " + isoDate);
        } catch (Exception e) {
            logError("Failed to parse date: " + isoDate + ", error: " + e.getMessage());
            e.printStackTrace();
        }

        return "0";
    }

    /**
     * 从 HTML 中提取纯文本内容
     * 用于显示被引用帖子的内容预览
     */
    private static String extractTextFromHtml(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }

        try {
            Document doc = Jsoup.parse(html);
            String text = doc.text();

            // 限制长度，避免引用内容过长
            if (text.length() > 200) {
                text = text.substring(0, 200) + "...";
            }

            return text;
        } catch (Exception e) {
            logError("Failed to extract text from HTML: " + e.getMessage());
            return "";
        }
    }

    private static void logDebug(String message) {
        try {
            android.util.Log.d("DiscourseConverter", message);
        } catch (RuntimeException e) {
            System.out.println("DiscourseConverter: " + message);
        }
    }

    private static void logWarn(String message) {
        try {
            android.util.Log.w("DiscourseConverter", message);
        } catch (RuntimeException e) {
            System.out.println("DiscourseConverter [WARN]: " + message);
        }
    }

    public static void logError(String message) {
        try {
            android.util.Log.e("DiscourseConverter", message);
        } catch (RuntimeException e) {
            System.out.println("DiscourseConverter [ERROR]: " + message);
        }
    }
}
