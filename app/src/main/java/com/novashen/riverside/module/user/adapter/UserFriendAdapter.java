package com.novashen.riverside.module.user.adapter;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.imageview.ShapeableImageView;
import com.novashen.riverside.R;
import com.novashen.riverside.api.discourse.entity.DiscourseFollowUser;
import com.novashen.riverside.util.TimeUtil;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/5 16:52
 */
public class UserFriendAdapter extends BaseQuickAdapter<DiscourseFollowUser, BaseViewHolder> {

    private static final String TAG = "UserFriendAdapter";

    public UserFriendAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiscourseFollowUser item) {
        String displayName = item.getName();
        if (displayName == null || displayName.trim().length() == 0) {
            displayName = item.getUsername();
        }

        helper.setText(R.id.item_user_friend_name, displayName == null ? "" : displayName);

        String lastSeenAt = item.getLastSeenAt();
        String lastSeenText = formatLastSeen(lastSeenAt);
        helper.getView(R.id.item_user_friend_last_login).setVisibility(android.view.View.VISIBLE);
        helper.setText(R.id.item_user_friend_last_login, "我也不知道他啥时候活跃");
        if (lastSeenText == null || lastSeenText.length() == 0) {
            helper.setText(R.id.item_user_friend_last_login, "我也不知道他啥时候活跃");
            Log.d(TAG, "user=" + item.getUsername() + ", last_activity=我也不知道他啥时候活跃");
        } else {
            helper.setText(R.id.item_user_friend_last_login, lastSeenText);
            Log.d(TAG, "user=" + item.getUsername() + ", last_activity=" + lastSeenText);
        }

        String avatarUrl = item.getAvatarUrl(120);
        Glide.with(mContext).load(avatarUrl).into((ShapeableImageView) helper.getView(R.id.item_user_friend_icon));
    }

    private String formatLastSeen(String isoTime) {
        if (isoTime == null || isoTime.trim().length() == 0) {
            return "";
        }
        long timestampMs = parseIsoToTimestampMs(isoTime);
        if (timestampMs <= 0) {
            Log.d(TAG, "Parse lastSeen failed, iso=" + isoTime);
            return "";
        }
        Log.d(TAG, "Parsed lastSeen: iso=" + isoTime + ", ms=" + timestampMs);
        return TimeUtil.formatTime(String.valueOf(timestampMs), R.string.last_login_time, mContext);
    }

    private long parseIsoToTimestampMs(String isoTime) {
        SimpleDateFormat[] formats = new SimpleDateFormat[] {
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        };
        for (SimpleDateFormat format : formats) {
            try {
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = format.parse(isoTime);
                if (date != null) {
                    return date.getTime();
                }
            } catch (Exception ignored) {
            }
        }
        return 0L;
    }
}
