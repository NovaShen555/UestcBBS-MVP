package com.novashen.riverside.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.novashen.riverside.annotation.UserPostType.TYPE_USER_FAVORITE;
import static com.novashen.riverside.annotation.UserPostType.TYPE_USER_POST;
import static com.novashen.riverside.annotation.UserPostType.TYPE_USER_REPLY;

@StringDef({TYPE_USER_POST, TYPE_USER_REPLY, TYPE_USER_FAVORITE})
@Retention(RetentionPolicy.SOURCE)
public @interface UserPostType {
    String TYPE_USER_POST = "topic";
    String TYPE_USER_REPLY = "reply";
    String TYPE_USER_FAVORITE = "favorite";
}
