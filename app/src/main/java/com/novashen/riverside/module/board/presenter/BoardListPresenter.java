package com.novashen.riverside.module.board.presenter;

import android.content.Context;

import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.api.discourse.entity.CategoriesResponse;
import com.novashen.riverside.api.discourse.entity.CategoryDetailResponse;
import com.novashen.riverside.entity.ForumListBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.board.model.BoardModel;
import com.novashen.riverside.module.board.model.DiscourseBoardModel;
import com.novashen.riverside.module.board.view.BoardListView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class BoardListPresenter extends BasePresenter<BoardListView> {

    private final BoardModel boardModel = new BoardModel();
    private final DiscourseBoardModel discourseBoardModel = new DiscourseBoardModel();
    private static ForumListBean cachedForumList;

    public void getForumList(Context context) {
        getForumList(context, true);
    }

    public void getForumList(Context context, boolean forceRefresh) {
        if (!forceRefresh && cachedForumList != null) {
            view.onGetBoardListSuccess(cachedForumList);
            return;
        }

        discourseBoardModel.getCategories(new Observer<CategoriesResponse>() {
            @Override
            public void OnSuccess(CategoriesResponse response) {
                if (response != null && response.categoryList != null && response.categoryList.categories != null) {
                    List<CategoriesResponse.Category> parentCategories = new ArrayList<>();
                    for (CategoriesResponse.Category category : response.categoryList.categories) {
                        if (category.parentCategoryId == null) {
                            parentCategories.add(category);
                        }
                    }
                    loadDiscourseBoards(parentCategories);
                } else {
                    view.onGetBoardListError("获取板块列表失败");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetBoardListError(e.message);
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

    private void loadDiscourseBoards(List<CategoriesResponse.Category> parentCategories) {
        ForumListBean forumListBean = new ForumListBean();
        forumListBean.list = new ArrayList<>();

        if (parentCategories == null || parentCategories.isEmpty()) {
            view.onGetBoardListSuccess(forumListBean);
            return;
        }

        int totalRequests = 0;
        List<List<ForumListBean.ListBean.BoardListBean>> boardLists = new ArrayList<>();

        for (CategoriesResponse.Category parent : parentCategories) {
            ForumListBean.ListBean listBean = new ForumListBean.ListBean();
            listBean.board_category_id = parent.id;
            listBean.board_category_name = parent.name;
            listBean.board_category_type = 0;
            listBean.board_list = new ArrayList<>();
            forumListBean.list.add(listBean);

            if (parent.subcategoryIds != null && !parent.subcategoryIds.isEmpty()) {
                totalRequests += parent.subcategoryIds.size();
                boardLists.add(listBean.board_list);
            } else {
                listBean.board_list.add(convertCategoryToBoard(parent));
                boardLists.add(listBean.board_list);
            }
        }

        if (totalRequests == 0) {
            view.onGetBoardListSuccess(forumListBean);
            return;
        }

        final int totalRequestsFinal = totalRequests;
        final int[] completedRequests = {0};

        for (int i = 0; i < parentCategories.size(); i++) {
            CategoriesResponse.Category parent = parentCategories.get(i);
            List<ForumListBean.ListBean.BoardListBean> targetList = boardLists.get(i);

            if (parent.subcategoryIds == null || parent.subcategoryIds.isEmpty()) {
                continue;
            }

            for (Integer subId : parent.subcategoryIds) {
                discourseBoardModel.getCategoryDetail(subId, new Observer<CategoryDetailResponse>() {
                    @Override
                    public void OnSuccess(CategoryDetailResponse response) {
                        if (response != null && response.category != null) {
                            targetList.add(convertCategoryDetailToBoard(response.category));
                        }
                        onCategoryDetailFinished(forumListBean, completedRequests, totalRequestsFinal);
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        onCategoryDetailFinished(forumListBean, completedRequests, totalRequestsFinal);
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
    }

    private void onCategoryDetailFinished(ForumListBean forumListBean,
                                          int[] completedRequests,
                                          int totalRequests) {
        completedRequests[0]++;
        if (completedRequests[0] == totalRequests) {
            cachedForumList = forumListBean;
            view.onGetBoardListSuccess(forumListBean);
        }
    }

    private ForumListBean.ListBean.BoardListBean convertCategoryToBoard(CategoriesResponse.Category category) {
        ForumListBean.ListBean.BoardListBean board = new ForumListBean.ListBean.BoardListBean();
        board.board_id = category.id;
        board.board_name = category.name;
        board.description = category.descriptionText;
        board.board_child = 0;
        board.board_img = null;
        board.last_posts_date = null;
        board.board_content = 0;
        board.forumRedirect = null;
        board.favNum = 0;
        board.td_posts_num = category.topicCount;
        board.topic_total_num = category.topicCount;
        board.posts_total_num = category.postCount;
        board.is_focus = 0;
        return board;
    }

    private ForumListBean.ListBean.BoardListBean convertCategoryDetailToBoard(CategoryDetailResponse.Category category) {
        ForumListBean.ListBean.BoardListBean board = new ForumListBean.ListBean.BoardListBean();
        board.board_id = category.id;
        board.board_name = category.name;
        board.description = category.descriptionText;
        board.board_child = 0;
        board.board_img = null;
        board.last_posts_date = null;
        board.board_content = 0;
        board.forumRedirect = null;
        board.favNum = 0;
        board.td_posts_num = category.topicCount;
        board.topic_total_num = category.topicCount;
        board.posts_total_num = category.postCount;
        board.is_focus = 0;
        return board;
    }

    public void getTotalPosts() {
        boardModel.getTotalPosts(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {

            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

}
