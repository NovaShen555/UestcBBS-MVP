package com.novashen.riverside.module.board.presenter;

import android.content.Context;

import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.entity.ForumListBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.board.model.BoardModel;
import com.novashen.riverside.module.board.view.BoardListView;

import io.reactivex.disposables.Disposable;

public class BoardListPresenter extends BasePresenter<BoardListView> {

    private BoardModel boardModel = new BoardModel();

    public void getForumList(Context context) {
        boardModel.getForumList(
                new Observer<ForumListBean>() {
                    @Override
                    public void OnSuccess(ForumListBean forumListBean) {
                        if (forumListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetBoardListSuccess(forumListBean);
                        }
                        if (forumListBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetBoardListError(forumListBean.head.errInfo);
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
