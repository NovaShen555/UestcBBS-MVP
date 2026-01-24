package com.novashen.riverside.module.user.presenter;

import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.user.model.UserModel;
import com.novashen.riverside.module.user.view.UserVisitorView;

import io.reactivex.disposables.Disposable;

public class UserVisitorPresenter extends BasePresenter<UserVisitorView> {
    UserModel userModel = new UserModel();

    public void deleteVisitedHistory(int uid, int position) {
        userModel.deleteVisitedHistory(uid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                view.onDeleteVisitedHistorySuccess("删除记录成功", position);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onDeleteVisitedHistoryError("删除失败：" + e.message);
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
