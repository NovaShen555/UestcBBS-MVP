package com.novashen.riverside.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.novashen.riverside.annotation.TaskType.TYPE_DOING;
import static com.novashen.riverside.annotation.TaskType.TYPE_NEW;
import static com.novashen.riverside.annotation.TaskType.TYPE_DONE;
import static com.novashen.riverside.annotation.TaskType.TYPE_FAILED;

/**
 * author: sca_tl
 * date: 2020/12/26 12:07
 * description:
 */
@StringDef({TYPE_NEW, TYPE_DOING, TYPE_DONE, TYPE_FAILED})
@Retention(RetentionPolicy.SOURCE)
public @interface TaskType {
    String TYPE_NEW = "new";
    String TYPE_DOING = "doing";
    String TYPE_DONE = "done";
    String TYPE_FAILED = "failed";
}
