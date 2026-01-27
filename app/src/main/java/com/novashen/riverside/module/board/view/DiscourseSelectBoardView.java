package com.novashen.riverside.module.board.view;

import com.novashen.riverside.api.discourse.entity.CategoriesResponse;
import com.novashen.riverside.api.discourse.entity.CategoryDetailResponse;
import com.novashen.riverside.base.BaseView;

import java.util.List;

/**
 * Discourse 板块选择 View 接口
 */
public interface DiscourseSelectBoardView extends BaseView {

    /**
     * 获取父级板块成功
     */
    void onGetParentCategoriesSuccess(List<CategoriesResponse.Category> categories);

    /**
     * 获取父级板块失败
     */
    void onGetParentCategoriesError(String msg);

    /**
     * 获取子板块成功
     */
    void onGetSubcategoriesSuccess(List<CategoryDetailResponse.Category> subcategories);
}
