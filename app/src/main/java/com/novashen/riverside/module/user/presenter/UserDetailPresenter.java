package com.novashen.riverside.module.user.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.novashen.riverside.R;
import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.entity.BlackUserBean;
import com.novashen.riverside.entity.FollowUserBean;
import com.novashen.riverside.entity.ModifyPswBean;
import com.novashen.riverside.entity.ModifySignBean;
import com.novashen.riverside.entity.UserDetailBean;
import com.novashen.riverside.entity.UserFriendBean;
import com.novashen.riverside.entity.VisitorsBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.api.discourse.entity.DiscourseUserResponse;
import com.novashen.riverside.api.discourse.entity.DiscourseUserSummaryResponse;
import com.novashen.riverside.module.user.model.DiscourseUserModel;
import com.novashen.riverside.module.user.model.UserModel;
import com.novashen.riverside.module.user.view.ModifyAvatarActivity;
import com.novashen.riverside.module.user.view.UserDetailView;
import com.novashen.riverside.module.webview.view.WebViewActivity;
import com.novashen.riverside.util.BBSLinkUtil;
import com.novashen.riverside.util.ClipBoardUtil;
import com.novashen.riverside.util.CommonUtil;
import com.novashen.riverside.util.Constant;
import com.novashen.riverside.util.SharePrefUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;


/**
 * author: sca_tl
 * description:
 * date: 2020/2/3 12:54
 */
public class UserDetailPresenter extends BasePresenter<UserDetailView> {

    private UserModel userModel = new UserModel();
    private DiscourseUserModel discourseUserModel = new DiscourseUserModel();

    public void getUidByName(String name) {
        userModel.getUserSpaceByName(name, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String url = document.select("div[class=wp cl]").select("div[id=nv]")
                            .select("ul").select("li").get(0).select("a").attr("href");
                    int uid = BBSLinkUtil.getLinkInfo(url).getId();
                    view.onGetSpaceByNameSuccess(uid);
                } catch (Exception e) {
                    view.onGetSpaceByNameError(e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetSpaceByNameError(e.message);
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

    public void getUserDetail(int uid, Context context) {
        userModel.getUserDetail(uid, new Observer<UserDetailBean>() {
            @Override
            public void OnSuccess(UserDetailBean userDetailBean) {
                if (userDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onGetUserDetailSuccess(userDetailBean);
                }
                if (userDetailBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onGetUserDetailError(userDetailBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUserDetailError(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
//                        SubscriptionManager.getInstance().add(d);
            }
        });
    }

    /**
     * 使用Discourse API获取用户详情
     * @param username 用户名
     * @param context 上下文
     */
    public void getDiscourseUserDetail(String username, Context context) {
        // 先获取用户基本信息
        discourseUserModel.getUserInfo(username, new Observer<DiscourseUserResponse>() {
            @Override
            public void OnSuccess(DiscourseUserResponse response) {
                if (response != null && response.getUser() != null) {
                    // 将Discourse用户信息转换为UserDetailBean
                    UserDetailBean userDetailBean = convertToUserDetailBean(response);

                    // Process medals
                    List<String> medalImages = new ArrayList<>();
                    if (response.getUser().getUserBadges() != null) {
                        for (DiscourseUserResponse.UserBadge userBadge : response.getUser().getUserBadges()) {
                            if (userBadge.getBadge() != null) {
                                String img = userBadge.getBadge().getImageUrl();
                                if (!TextUtils.isEmpty(img)) {
                                     if (!img.startsWith("http")) {
                                         img = "https://river-side.cc" + (img.startsWith("/") ? "" : "/") + img;
                                     }
                                     medalImages.add(img);
                                }
                            }
                        }
                    }
                    view.onGetUserSpaceSuccess(new ArrayList<>(), medalImages);

                    // 再获取用户摘要信息（包含帖子数和回复数）
                    discourseUserModel.getUserSummary(username, new Observer<DiscourseUserSummaryResponse>() {
                        @Override
                        public void OnSuccess(DiscourseUserSummaryResponse summaryResponse) {
                            if (summaryResponse != null && summaryResponse.getUserSummary() != null) {
                                // 更新帖子数和回复数
                                DiscourseUserSummaryResponse.UserSummary summary = summaryResponse.getUserSummary();
                                userDetailBean.topic_num = summary.getTopicCount();
                                userDetailBean.reply_posts_num = summary.getPostCount();
                            }
                            view.onGetUserDetailSuccess(userDetailBean);
                        }

                        @Override
                        public void onError(ExceptionHelper.ResponseThrowable e) {
                            // 即使获取摘要失败，也显示基本信息
                            view.onGetUserDetailSuccess(userDetailBean);
                        }

                        @Override
                        public void OnCompleted() {

                        }

                        @Override
                        public void OnDisposable(Disposable d) {
                            disposable.add(d);
                        }
                    });
                } else {
                    view.onGetUserDetailError("用户信息为空");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUserDetailError(e.message);
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

    /**
     * 将Discourse用户响应转换为UserDetailBean
     */
    private UserDetailBean convertToUserDetailBean(DiscourseUserResponse response) {
        UserDetailBean bean = new UserDetailBean();
        DiscourseUserResponse.User user = response.getUser();

        bean.rs = ApiConstant.Code.SUCCESS_CODE;
        bean.name = user.getUsername();
        bean.icon = user.getAvatarUrl(240); // 使用240px的头像
        bean.userTitle = user.getTitle() != null ? user.getTitle() : "Lv." + user.getTrustLevel();
        bean.level = user.getTrustLevel();
        bean.sign = user.getBioRaw();

        // 使用Discourse的关注和粉丝数据
        bean.friend_num = user.getTotalFollowing();
        bean.follow_num = user.getTotalFollowers();

        // Prepare profile list
        bean.body = new UserDetailBean.BodyBean();
        bean.body.profileList = new ArrayList<>();

        if (user.getCreatedAt() != null) {
            UserDetailBean.BodyBean.ProfileListBean profile = new UserDetailBean.BodyBean.ProfileListBean();
            profile.type = "text_start"; // Assuming "text_start" or similar is used for plain text
            profile.title = "加入时间";
            profile.data = formatDate(user.getCreatedAt());
            bean.body.profileList.add(profile);
        }

        if (user.getLastSeenAt() != null) {
            UserDetailBean.BodyBean.ProfileListBean profile = new UserDetailBean.BodyBean.ProfileListBean();
            profile.type = "text_start";
            profile.title = "最后登录";
            profile.data = formatDate(user.getLastSeenAt());
            bean.body.profileList.add(profile);
        }

        if (user.getLocation() != null) {
             UserDetailBean.BodyBean.ProfileListBean profile = new UserDetailBean.BodyBean.ProfileListBean();
             profile.type = "text_start";
             profile.title = "位置";
             profile.data = user.getLocation();
             bean.body.profileList.add(profile);
        }

        if (user.getWebsite() != null) {
             UserDetailBean.BodyBean.ProfileListBean profile = new UserDetailBean.BodyBean.ProfileListBean();
             profile.type = "text_start";
             profile.title = "网站";
             profile.data = user.getWebsite();
             bean.body.profileList.add(profile);
        }

        // 初始化其他字段为默认值
        bean.is_black = 0;
        bean.is_follow = 0;
        bean.topic_num = 0;
        bean.reply_posts_num = 0;

        return bean;
    }

    public void followUser(int uid, String type, Context context) {
        userModel.followUser(uid, type, new Observer<FollowUserBean>() {
            @Override
            public void OnSuccess(FollowUserBean followUserBean) {
                if (followUserBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onFollowUserSuccess(followUserBean);
                }
                if (followUserBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onFollowUserError(followUserBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onFollowUserError(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
//                        SubscriptionManager.getInstance().add(d);
            }
        });
    }

    public void blackUser(int uid, String type, Context context) {
        userModel.blackUser(uid, type, new Observer<BlackUserBean>() {
            @Override
            public void OnSuccess(BlackUserBean blackUserBean) {
                if (blackUserBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onBlackUserSuccess(blackUserBean);
                }
                if (blackUserBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onBlackUserError(blackUserBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onBlackUserError(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
//                        SubscriptionManager.getInstance().add(d);
            }
        });
    }

    public void modifySign(String sign, Context context) {
        userModel.modifySign("info", sign, new Observer<ModifySignBean>() {
            @Override
            public void OnSuccess(ModifySignBean modifySignBean) {
                if (modifySignBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onModifySignSuccess(modifySignBean, sign);
                }
                if (modifySignBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onModifySignError(modifySignBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onModifySignError(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
//                        SubscriptionManager.getInstance().add(d);
            }
        });
    }

    public void modifyPsw(String oldPsw, String newPsw, Context context) {
        userModel.modifyPsw("password", oldPsw, newPsw, new Observer<ModifyPswBean>() {
            @Override
            public void OnSuccess(ModifyPswBean modifyPswBean) {
                if (modifyPswBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onModifyPswSuccess(modifyPswBean);
                }
                if (modifyPswBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onModifyPswError(modifyPswBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onModifyPswError(e.message);
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

    public void getUserSpace(int uid, Context context) {
        userModel.getUserSpace(uid, "", new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {

                    Document document = Jsoup.parse(s);
                    Elements visitor_elements = document.select("div[id=visitor]").select("ul[class=ml mls cl]").select("li");

                    List<VisitorsBean> visitorsBeans = new ArrayList<>();
                    for (int i = 0; i < visitor_elements.size(); i ++) {
                        VisitorsBean visitorsBean = new VisitorsBean();
                        visitorsBean.visitedTime = visitor_elements.get(i).select("span[class=xg2]").text();
                        visitorsBean.visitorName = visitor_elements.get(i).select("p").select("a").text();
                        visitorsBean.visitorUid = BBSLinkUtil.getLinkInfo(visitor_elements.get(i).select("p").select("a").attr("href")).getId();
                        visitorsBean.visitorAvatar = Constant.USER_AVATAR_URL + visitorsBean.visitorUid;
                        visitorsBeans.add(visitorsBean);
                    }

                    List<String> medalImages = new ArrayList<>();
                    Elements medal_elements = document.select("p[class=md_ctrl]").select("a").select("img");
                    for (int i = 0; i < medal_elements.size(); i ++) {
                        medalImages.add(ApiConstant.BBS_BASE_URL + medal_elements.get(i).attr("src"));
                    }
                    view.onGetUserSpaceSuccess(visitorsBeans, medalImages);
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

    public void getUserFriend(int uid, String type, Context context) {
        userModel.getUserFriend(1, 1000, uid, type, new Observer<UserFriendBean>() {
            @Override
            public void OnSuccess(UserFriendBean userFriendBean) {
                if (userFriendBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onGetUserFriendSuccess(userFriendBean);
                }

                if (userFriendBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onGetUserFriendError(userFriendBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUserFriendError(e.message);
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

    public void showModifyInfoDialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_modify_user_info, new LinearLayout(context));
        LinearLayout modifyPsw = dialogView.findViewById(R.id.dialog_modify_user_info_modify_psw_layout);
        LinearLayout modifySign = dialogView.findViewById(R.id.dialog_modify_user_info_modify_sign_layout);
        LinearLayout modifyOther = dialogView.findViewById(R.id.dialog_modify_user_info_modify_other_layout);
        LinearLayout modifyAvatar = dialogView.findViewById(R.id.dialog_modify_user_info_modify_avatar_layout);
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create();
        dialog.show();
        modifyAvatar.setOnClickListener(v -> {
            dialog.dismiss();
            context.startActivity(new Intent(context, ModifyAvatarActivity.class));
        });
        modifySign.setOnClickListener(v -> {
            dialog.dismiss();
            showModifySignDialog("", context);
        });
        modifyPsw.setOnClickListener(v -> {
            dialog.dismiss();
            showModifyPswDialog(context);
        });
        modifyOther.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, "http://bbs.uestc.edu.cn/mobcent/app/web/index.php?r=user/userinfoadminview" +
                    "&accessToken=" + SharePrefUtil.getToken(context) +
                    "&accessSecret=" + SharePrefUtil.getSecret(context) +
                    "&act=info");
            context.startActivity(intent);
        });
    }

    public void showModifyPswDialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_modify_psw, new LinearLayout(context));
        EditText oldPsw = dialogView.findViewById(R.id.dialog_modify_psw_old_psw);
        EditText newPsw = dialogView.findViewById(R.id.dialog_modify_psw_new_psw);
        EditText confirmPsw = dialogView.findViewById(R.id.dialog_modify_psw_confirm_psw);

        CommonUtil.showSoftKeyboard(context, oldPsw, 1);
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setView(dialogView)
                .setTitle("修改密码")
                .create();
        dialog.setOnShowListener(d -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                if (TextUtils.isEmpty(oldPsw.getText().toString())) {
                    view.onModifyPswError("请输入旧密码");
                } else if (TextUtils.isEmpty(newPsw.getText().toString())){
                    view.onModifyPswError("请输入新密码");
                } else if (TextUtils.isEmpty(confirmPsw.getText().toString())) {
                    view.onModifyPswError("请确认密码");
                } else if (!newPsw.getText().toString().equals(confirmPsw.getText().toString())){
                    view.onModifyPswError("新密码不一致");
                } else {
                    modifyPsw(oldPsw.getText().toString(), newPsw.getText().toString(), context);
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }


    public void showModifySignDialog(String sign, Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_modify_sign, new LinearLayout(context));
        EditText content = dialogView.findViewById(R.id.dialog_modify_sign_content);
        CommonUtil.showSoftKeyboard(context, content, 1);
        content.setText(sign);
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setView(dialogView)
                .setTitle("修改签名")
                .create();
        dialog.setOnShowListener(d -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                if (TextUtils.isEmpty(content.getText().toString())) {
                    view.onModifySignError("请输入签名内容");
                } else {
                    modifySign(content.getText().toString(), context);
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }

    public void showUserSignDialog(String sign, Context context) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("查看签名")
                .setPositiveButton("复制", null)
                .setMessage(sign)
                .create();
        dialog.setOnShowListener(d -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                ClipBoardUtil.copyToClipBoard(context, sign);
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    /**
     * author: sca_tl
     * description: 展示用户资料
     */
    public void showUserInfo(UserDetailBean userDetailBean, boolean property, Context context) {
        AlertDialog user_info_dialog = new MaterialAlertDialogBuilder(context)
//                .setPositiveButton("确认", null)
//                .setNegativeButton("取消", null)
                .setTitle(property ? "财富信息" : "其它资料")
                .create();
        if (!property) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < userDetailBean.body.profileList.size(); i ++) {
                String title = userDetailBean.body.profileList.get(i).title;
                String data = userDetailBean.body.profileList.get(i).data + "";
                builder.append(title).append("：").append(data).append("\n");
            }
            user_info_dialog.setMessage(builder);

        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < userDetailBean.body.creditList.size(); i ++) {
                String title = userDetailBean.body.creditList.get(i).title;
                String data = userDetailBean.body.creditList.get(i).data + "";
                builder.append(title).append("：").append(data).append("\n");
            }
            user_info_dialog.setMessage(builder);
        }

        user_info_dialog.show();

    }

    public void showBlackConfirmDialog(Context context, int uid) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setTitle("加入黑名单")
                .setMessage(context.getString(R.string.black_list_desp))
                .create();
        dialog.setOnShowListener(d -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                blackUser(uid, "black", context);
                dialog.dismiss();
            });
        });
        dialog.show();
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

}
