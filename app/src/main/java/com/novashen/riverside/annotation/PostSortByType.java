package com.novashen.riverside.annotation;

import androidx.annotation.StringDef;


import static com.novashen.riverside.annotation.PostSortByType.TYPE_NEW;
import static com.novashen.riverside.annotation.PostSortByType.TYPE_ALL;
import static com.novashen.riverside.annotation.PostSortByType.TYPE_HOT;
import static com.novashen.riverside.annotation.PostSortByType.TYPE_ESSENCE;

@StringDef({TYPE_NEW, TYPE_ALL, TYPE_HOT, TYPE_ESSENCE})
public @interface PostSortByType {
    String TYPE_NEW = "new";
    String TYPE_ALL = "all";
    String TYPE_HOT = "hot";
    String TYPE_ESSENCE = "essence";
}
