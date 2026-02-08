package com.novashen.riverside.module.user.presenter;

import android.content.Context;

import android.util.Log;

import com.novashen.riverside.annotation.UserFriendType;
import com.novashen.riverside.api.discourse.entity.DiscourseFollowUser;
import com.novashen.riverside.api.discourse.entity.DiscourseUserResponse;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.user.model.DiscourseUserModel;
import com.novashen.riverside.module.user.view.UserFriendView;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/5 16:42
 */
public class UserFriendPresenter extends BasePresenter<UserFriendView> {

    private static final String TAG = "UserFriendPresenter";
    private DiscourseUserModel discourseUserModel = new DiscourseUserModel();

    public void getUserFriend(String username, String type, Context context) {
        if (UserFriendType.TYPE_FOLLOWED.equals(type)) {
            discourseUserModel.getUserFollowers(username, new Observer<List<DiscourseFollowUser>>() {
                @Override
                public void OnSuccess(List<DiscourseFollowUser> userList) {
                    Log.d(TAG, "Followers loaded, size=" + (userList == null ? 0 : userList.size()));
                    view.onGetUserFriendSuccess(userList);
                }

                @Override
                public void onError(ExceptionHelper.ResponseThrowable e) {
                    view.onGetUserFriendError(e.message);
                }

                @Override
                public void OnCompleted() {

                }

                @Override
                public void OnDisposable(Disposable d) {
                    disposable.add(d);
                }
            });
        } else if (UserFriendType.TYPE_FOLLOW.equals(type)) {
            discourseUserModel.getUserFollowing(username, new Observer<List<DiscourseFollowUser>>() {
                @Override
                public void OnSuccess(List<DiscourseFollowUser> userList) {
                    Log.d(TAG, "Following loaded, size=" + (userList == null ? 0 : userList.size()));
                    view.onGetUserFriendSuccess(userList);
                }

                @Override
                public void onError(ExceptionHelper.ResponseThrowable e) {
                    view.onGetUserFriendError(e.message);
                }

                @Override
                public void OnCompleted() {

                }

                @Override
                public void OnDisposable(Disposable d) {
                    disposable.add(d);
                }
            });
        } else {
            view.onGetUserFriendError("当前仅支持粉丝和关注列表");
        }
    }

    public void getUserLastSeen(String username, int position) {
        Log.d(TAG, "Request user detail: username=" + username + ", position=" + position);
        discourseUserModel.getUserInfo(username, new Observer<DiscourseUserResponse>() {
            @Override
            public void OnSuccess(DiscourseUserResponse response) {
                if (response != null && response.getUser() != null) {
                    String lastSeenAt = response.getUser().getLastSeenAt();
                    Log.d(TAG, "User detail loaded: username=" + response.getUser().getUsername()
                            + ", lastSeenAt=" + lastSeenAt + ", position=" + position);
                    view.onUserLastSeenLoaded(position, lastSeenAt);
                } else {
                    Log.d(TAG, "User detail empty: username=" + username + ", position=" + position);
                    view.onUserLastSeenLoaded(position, null);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                Log.d(TAG, "User detail error: username=" + username + ", position=" + position
                        + ", error=" + e.message);
                view.onUserLastSeenLoaded(position, null);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

}
