package com.novashen.riverside.module.medal.presenter;

import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.entity.MedalBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.medal.model.MedalModel;
import com.novashen.riverside.module.medal.view.MedalCenterView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/10/7 18:59
 * description:
 */
public class MedalCenterPresenter extends BasePresenter<MedalCenterView> {
    private MedalModel medalModel = new MedalModel();

    public void getMedalCenter() {
        medalModel.getMedalCenter(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {

                    Document document = Jsoup.parse(s);
                    Elements elements = document.select("ul[class=mtm mgcl cl]").select("li");
                    String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");

                    MedalBean medalBean = new MedalBean();
                    medalBean.medalCenterBeans = new ArrayList<>();
                    for (Element e : elements) {
                        MedalBean.MedalCenterBean medalCenterBean = new MedalBean.MedalCenterBean();
                        medalCenterBean.medalDsp = e.select("div[class=tip_c]").text();
                        medalCenterBean.medalIcon = ApiConstant.BBS_BASE_URL + e.select("img").attr("src");
                        medalCenterBean.medalName = e.select("p[class=xw1]").text();
                        medalCenterBean.medalId = Integer.parseInt(e.select("div[class=mg_img]").attr("id").replace("medal_", ""));
                        medalCenterBean.buyDsp = e.select("a[class=xi2]").text();
                        medalBean.medalCenterBeans.add(medalCenterBean);
                    }
                    Collections.reverse(medalBean.medalCenterBeans);
                    view.onGetMedalCenterDataSuccess(medalBean);

                } catch (Exception e) {
                    view.onGetMedalCenterDataError("获取勋章信息失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetMedalCenterDataError("获取勋章信息失败：" + e.getMessage());
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

    public void getMineMedal() {
        medalModel.getMineMedal(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {

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
