package com.novashen.riverside.module.post.presenter;

import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.entity.ViewVoterBean;
import com.novashen.riverside.entity.VoteOptionsBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import com.novashen.riverside.module.post.model.PostModel;
import com.novashen.riverside.module.post.view.ViewVoterView;
import com.novashen.riverside.util.BBSLinkUtil;
import com.novashen.riverside.util.Constant;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class ViewVoterPresenter extends BasePresenter<ViewVoterView> {
    PostModel postModel = new PostModel();

    public void getVoteOptions(int tid) {
        postModel.getVoteOptions(tid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

                try {

                    Document document = Jsoup.parse(s);
                    if (s.contains("messagetext")) {
                        String msg = document.select("div[id=messagetext]").text();
                        view.onGetVoteOptionsError(msg);
                    } else {

                        List<VoteOptionsBean> voteOptionsBeanList = new ArrayList<>();

                        Elements elements = document.select("div[class=c voterlist]").select("p").select("select[class=ps]").select("option");
                        for (int i = 0; i < elements.size(); i ++) {
                            VoteOptionsBean voteOptionsBean = new VoteOptionsBean();
                            voteOptionsBean.optionName = elements.get(i).text();
                            voteOptionsBean.optionId = Integer.parseInt(elements.get(i).attr("value"));

                            voteOptionsBeanList.add(voteOptionsBean);
                        }

                        view.onGetVoteOptionsSuccess(voteOptionsBeanList);

                    }

                } catch (Exception e) {
                    view.onGetVoteOptionsError("获取投票选项失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetVoteOptionsError("获取投票选项失败：" + e.message);
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

    public void viewVoters(int tid, int optionsId, int page) {
        postModel.viewVoter(tid, optionsId, page, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    Elements elements = document.select("ul[class=ml mtm cl voterl]").select("li");

                    if (elements.get(0).text().equals("无")) {
                        view.onGetVotersError("该选项还没人投票");
                    } else {
                        List<ViewVoterBean> viewVoterBeans = new ArrayList<>();
                        for (int i = 0; i < elements.size(); i ++) {
                            ViewVoterBean viewVoterBean = new ViewVoterBean();
                            viewVoterBean.name = elements.get(i).select("a").text();
                            viewVoterBean.uid = BBSLinkUtil.getLinkInfo(elements.get(i).select("a").attr("href")).getId();
                            viewVoterBean.avatar = Constant.USER_AVATAR_URL + viewVoterBean.uid;

                            viewVoterBeans.add(viewVoterBean);
                        }
                        view.onGetVotersSuccess(viewVoterBeans, s.contains("下一页"));
                    }


                } catch (Exception e) {
                    view.onGetVoteOptionsError("获取投票人失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetVotersError("获取投票人失败：" + e.message);
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
