package com.novashen.riverside.module.post.view;

import com.novashen.riverside.entity.ViewVoterBean;
import com.novashen.riverside.entity.VoteOptionsBean;

import java.util.List;

public interface ViewVoterView {
    void onGetVoteOptionsSuccess(List<VoteOptionsBean> voteOptionsBeans);
    void onGetVoteOptionsError(String msg);
    void onGetVotersSuccess(List<ViewVoterBean> viewVoterBeans, boolean hasNext);
    void onGetVotersError(String msg);
}
