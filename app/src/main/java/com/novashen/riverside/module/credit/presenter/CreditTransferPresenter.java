package com.novashen.riverside.module.credit.presenter;

import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.credit.model.CreditModel;
import com.novashen.riverside.module.credit.view.CreditTransferView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.disposables.Disposable;

public class CreditTransferPresenter extends BasePresenter<CreditTransferView> {
    CreditModel creditModel = new CreditModel();

    public void getCreditFormHash() {
        creditModel.getCreditFormHash(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("先登录才能")) {
                    view.onGetFormHashError("请获取Cookies后再进行本操作");
                } else {
                    try {
                        Document document = Jsoup.parse(s);
                        String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");

                        view.onGetFormHashSuccess(formHash);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetFormHashError("请求失败：\n" + e.message);
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

    public void creditTransfer(String formHash, String amount, String toUserName, String password, String message){
        creditModel.creditTransfer(formHash, amount, toUserName, password, message, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("messagetext")) {
                    String msg = Jsoup.parse(s).select("div[id=messagetext]").text();
                    if (s.contains("积分转帐成功")) {
                        view.onTransferSuccess("转账成功");
                    } else {
                        view.onTransferError(msg);
                    }
                } else {
                    view.onTransferError("出现了一个问题，请查看转账记录确认是否转账成功");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onTransferError("转账失败：" + e.message);
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
