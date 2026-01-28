package com.novashen.riverside.module.user.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.novashen.riverside.R;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.user.model.UserModel;
import com.novashen.riverside.module.user.model.DiscourseUserModel;
import com.novashen.riverside.api.discourse.entity.DiscourseUserResponse;
import com.novashen.riverside.api.discourse.entity.DiscourseUserSummaryResponse;
import com.novashen.riverside.module.user.view.UserMainPageView;
import com.novashen.riverside.util.TimeUtil;

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
    private DiscourseUserModel discourseUserModel = new DiscourseUserModel();

    /**
     * Get user space info via Discourse API
     */
    public void getDiscourseUserSpace(String username, Context context) {
         discourseUserModel.getUserInfo(username, new Observer<DiscourseUserResponse>() {
             @Override
             public void OnSuccess(DiscourseUserResponse response) {
                 if (response != null && response.getUser() != null) {
                     DiscourseUserResponse.User user = response.getUser();
                     
                     String registerTime = formatDate(user.getCreatedAt());
                     String lastLoginTime = formatDate(user.getLastSeenAt());
                     String location = user.getLocation() != null ? user.getLocation() : "未知";

                     // Get Summary for read time
                     discourseUserModel.getUserSummary(username, new Observer<DiscourseUserSummaryResponse>() {
                        @Override
                        public void OnSuccess(DiscourseUserSummaryResponse summaryResponse) {
                            String onlineTime = "0分钟";
                            if (summaryResponse != null && summaryResponse.getUserSummary() != null) {
                                int minutes = summaryResponse.getUserSummary().getTimeRead();
                                // User instruction: unit is minutes
                                if (minutes < 60) {
                                    onlineTime = minutes + "分钟";
                                } else {
                                    onlineTime = (minutes / 60) + "小时" + (minutes % 60) + "分钟";
                                }
                            }
                            if (view != null) {
                                view.onGetUserSpaceSuccess(onlineTime, registerTime, lastLoginTime, location);
                            }
                        }

                        @Override
                        public void onError(ExceptionHelper.ResponseThrowable e) {
                             // Summary failed, return info without read time
                             if (view != null) {
                                view.onGetUserSpaceSuccess("未知", registerTime, lastLoginTime, location);
                             }
                        }

                        @Override
                        public void OnCompleted() { }

                        @Override
                        public void OnDisposable(Disposable d) { disposable.add(d); }
                     });
                     
                 } else {
                     if (view != null) view.onGetUserSpaceError("用户数据为空");
                 }
             }

             @Override
             public void onError(ExceptionHelper.ResponseThrowable e) {
                 if (view != null) view.onGetUserSpaceError(e.message);
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

    private String formatDate(String isoDate) {
        if (TextUtils.isEmpty(isoDate)) return "";
        try {
             String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
             java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat(pattern, java.util.Locale.US);
             inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
             java.util.Date date = inputFormat.parse(isoDate);
             
             java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
             return outputFormat.format(date);
        } catch (Exception e) {
             return isoDate;
        }
    }


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

                    // View interface changed: removed isOnline boolean
                    view.onGetUserSpaceSuccess(onLineTime, registerTime, lastLoginTime, ipLocation);
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
