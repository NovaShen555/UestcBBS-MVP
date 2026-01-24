package com.novashen.riverside.module.user.presenter;

import android.content.Context;

import com.novashen.riverside.R;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.user.model.UserModel;
import com.novashen.riverside.module.user.view.UserMainPageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2021/3/17 13:02
 * description:
 */
public class UserMainPagePresenter extends BasePresenter<UserMainPageView> {
    private UserModel userModel = new UserModel();

    public void getUserSpace(int uid, Context context) {
        userModel.getUserSpace(uid, "profile", new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {

                    Document document = Jsoup.parse(s);
                    Elements elements = document.select("div[class=bm_c u_profile]").select("div[class=pbm mbm bbda cl]");

                    boolean isOnline = elements.get(0).select("h2[class=mbn]").html().contains("在线");

                    String onLineTime = "";
                    String registerTime = "";
                    String lastLoginTime = "";
                    String ipLocation = "";

                    for (int i = 0; i < elements.size(); i ++) {
                        if (elements.get(i).html().contains("活跃概况")) {
                            onLineTime = elements.get(i).select("ul[class=pf_l]").select("li").get(0).ownText();
                            registerTime = elements.get(i).select("ul[class=pf_l]").select("li").get(1).ownText();
                            lastLoginTime = elements.get(i).select("ul[class=pf_l]").select("li").get(2).ownText();

                            String ipAddress = elements.get(i).select("ul[class=pf_l]").select("li").get(4).ownText();
                            Matcher matcher = Pattern.compile("(.*?)( - - )(.*?)").matcher(ipAddress);
                            if (matcher.matches()) {
                                ipLocation = matcher.group(3);
                            }

                            break;
                        }
                    }

                    view.onGetUserSpaceSuccess(isOnline, onLineTime, registerTime, lastLoginTime, ipLocation);
                } catch (Exception e) {
                    view.onGetUserSpaceError(e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUserSpaceError(e.message);
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
