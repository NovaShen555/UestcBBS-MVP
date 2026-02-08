package com.novashen.riverside.module.user.view;

import com.novashen.riverside.api.discourse.entity.DiscourseFollowUser;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/5 16:42
 */
public interface UserFriendView {
    void onGetUserFriendSuccess(List<DiscourseFollowUser> userList);
    void onGetUserFriendError(String msg);
    void onUserLastSeenLoaded(int position, String lastSeenAt);
}
