package com.novashen.riverside.module.board.presenter;

import com.novashen.riverside.api.discourse.entity.CategoriesResponse;
import com.novashen.riverside.api.discourse.entity.CategoryDetailResponse;
import com.novashen.riverside.base.BaseVBPresenter;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.board.model.DiscourseBoardModel;
import com.novashen.riverside.module.board.view.DiscourseSelectBoardView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Discourse 板块选择 Presenter
 */
public class DiscourseSelectBoardPresenter extends BaseVBPresenter<DiscourseSelectBoardView> {

    private final DiscourseBoardModel boardModel = new DiscourseBoardModel();

    /**
     * 获取所有父级板块
     */
    public void getParentCategories() {
        boardModel.getCategories(new Observer<CategoriesResponse>() {
            @Override
            public void OnSuccess(CategoriesResponse response) {
                if (response.categoryList != null && response.categoryList.categories != null) {
                    // 过滤出父级板块（没有 parent_category_id 的板块）
                    List<CategoriesResponse.Category> parentCategories = new ArrayList<>();
                    for (CategoriesResponse.Category category : response.categoryList.categories) {
                        if (category.parentCategoryId == null) {
                            parentCategories.add(category);
                        }
                    }
                    getMView().onGetParentCategoriesSuccess(parentCategories);
                } else {
                    getMView().onGetParentCategoriesError("获取板块列表失败");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                getMView().onGetParentCategoriesError(e.message);
            }

            @Override
            public void OnCompleted() {
            }

            @Override
            public void OnDisposable(Disposable d) {
                getMCompositeDisposable().add(d);
            }
        });
    }

    /**
     * 获取子板块详情
     */
    public void getSubcategories(List<Integer> subcategoryIds) {
        if (subcategoryIds == null || subcategoryIds.isEmpty()) {
            getMView().onGetSubcategoriesSuccess(new ArrayList<>());
            return;
        }

        List<CategoryDetailResponse.Category> subcategories = new ArrayList<>();
        final int[] completedCount = {0};

        for (Integer subcategoryId : subcategoryIds) {
            boardModel.getCategoryDetail(subcategoryId, new Observer<CategoryDetailResponse>() {
                @Override
                public void OnSuccess(CategoryDetailResponse response) {
                    if (response.category != null) {
                        subcategories.add(response.category);
                    }
                    completedCount[0]++;
                    if (completedCount[0] == subcategoryIds.size()) {
                        getMView().onGetSubcategoriesSuccess(subcategories);
                    }
                }

                @Override
                public void onError(ExceptionHelper.ResponseThrowable e) {
                    completedCount[0]++;
                    if (completedCount[0] == subcategoryIds.size()) {
                        getMView().onGetSubcategoriesSuccess(subcategories);
                    }
                }

                @Override
                public void OnCompleted() {
                }

                @Override
                public void OnDisposable(Disposable d) {
                    getMCompositeDisposable().add(d);
                }
            });
        }
    }
}
