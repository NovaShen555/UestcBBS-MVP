package com.novashen.riverside.module.houqin.presenter;

import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.entity.HouQinReportListBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.houqin.model.HouQinModel;
import com.novashen.riverside.module.houqin.view.HouQinReportListView;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/10/24 11:18
 * description:
 */
public class HouQinReportListPresenter extends BasePresenter<HouQinReportListView> {
    HouQinModel houQinModel = new HouQinModel();

    public void getAllReportList(int pageNo) {
        houQinModel.getAllReportList(pageNo, new Observer<HouQinReportListBean>() {
            @Override
            public void OnSuccess(HouQinReportListBean houQinReportListBean) {
                view.onGetReportListSuccess(houQinReportListBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetReportListError("获取列表失败：" + e.message);
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
