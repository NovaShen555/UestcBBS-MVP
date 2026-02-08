package com.novashen.riverside.module.post.adapter;

import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novashen.riverside.R;
import com.novashen.riverside.entity.PostDetailBean;

import java.util.ArrayList;
import java.util.List;

public class ContentViewPollAdapter extends BaseQuickAdapter<PostDetailBean.TopicBean.PollInfoBean.PollItemListBean, BaseViewHolder> {
    private int total;
    private int poll_status;
    private List<Integer> ids = new ArrayList<>();
    private boolean singleChoice = false;

    public ContentViewPollAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void addPollData(List<PostDetailBean.TopicBean.PollInfoBean.PollItemListBean> data, int total, int poll_status, boolean singleChoice) {
        setNewData(data);
        this.total = total;
        this.poll_status = poll_status;
        this.singleChoice = singleChoice;
        ids.clear();
        if (data != null) {
            for (PostDetailBean.TopicBean.PollInfoBean.PollItemListBean item : data) {
                if (item.chosen) {
                    ids.add(item.poll_item_id);
                }
            }
        }
    }

    public List<Integer> getPollItemIds() {
        return ids;
    }

    @Override
    protected void convert(BaseViewHolder helper, final PostDetailBean.TopicBean.PollInfoBean.PollItemListBean item) {
        CheckBox checkBox = helper.getView(R.id.item_poll_checkbox);
        ProgressBar progressBar = helper.getView(R.id.item_poll_progress);

        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(ids.contains(item.poll_item_id));

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (singleChoice) {
                    ids.clear();
                }
                if (! ids.contains(item.poll_item_id)) {
                    ids.add(item.poll_item_id);
                }
            } else {
                if (ids.contains(item.poll_item_id)) {
                    ids.remove(Integer.valueOf(item.poll_item_id));
                }
            }
            if (singleChoice) {
                helper.itemView.post(this::notifyDataSetChanged);
            }
        });


        checkBox.setEnabled(poll_status == 2);
        String titleText = item.name;
        boolean showResult = total > 0 || item.total_num > 0 || (item.votes > 0);
        if (showResult) {
            int count = item.votes > 0 ? item.votes : item.total_num;
            String percent = item.percent;
            checkBox.setText(mContext.getString(R.string.vote_item_voted_num,
                    titleText, count, percent));
        } else {
            checkBox.setText(titleText);
        }

        if (showResult) {
            progressBar.setVisibility(android.view.View.VISIBLE);
            progressBar.setMax(total * 100);
            int progressTarget = (item.votes > 0 ? item.votes : item.total_num) * 100;
            progressBar.postDelayed(() -> {
                ValueAnimator animator = ValueAnimator.ofInt(0, progressTarget).setDuration(500);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addUpdateListener(animation -> progressBar.setProgress((int)animation.getAnimatedValue()));
                animator.start();
            }, 500);
        } else {
            progressBar.setVisibility(android.view.View.GONE);
        }

    }
}
