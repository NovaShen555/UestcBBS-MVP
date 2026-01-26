package com.novashen.riverside.module.post.model;

import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil;
import com.novashen.riverside.api.discourse.converter.DiscoursePostDetailConverter;
import com.novashen.riverside.api.discourse.entity.CreatePostResponse;
import com.novashen.riverside.api.discourse.entity.TopicDetailResponse;
import com.novashen.riverside.entity.PostDetailBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Discourse 帖子详情 Model
 * 使用 Discourse API 获取帖子详情
 */
public class DiscoursePostModel {

    /**
     * 获取帖子详情
     * @param topicId 帖子ID
     * @param observer 观察者
     */
    public void getPostDetail(int topicId, Observer<PostDetailBean> observer) {
        DiscourseRetrofitUtil.getInstance()
                .getApiService()
                .getTopicDetail(topicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    // 将 Discourse API 响应转换为 PostDetailBean
                    return DiscoursePostDetailConverter.convert(response);
                })
                .subscribe(new io.reactivex.Observer<PostDetailBean>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        observer.OnDisposable(d);
                    }

                    @Override
                    public void onNext(PostDetailBean postDetailBean) {
                        observer.OnSuccess(postDetailBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(ExceptionHelper.handleException(e));
                    }

                    @Override
                    public void onComplete() {
                        observer.OnCompleted();
                    }
                });
    }

    /**
     * 发表评论
     * @param content 评论内容
     * @param topicId 帖子ID
     * @param categoryId 分类ID
     * @param replyToPostNumber 回复的楼层号（可选，null表示直接回复主题）
     * @param observer 观察者
     */
    public void createPost(String content, int topicId, int categoryId, Integer replyToPostNumber, Observer<CreatePostResponse> observer) {
        // 创建请求体
        com.novashen.riverside.api.discourse.entity.CreatePostRequest request =
                new com.novashen.riverside.api.discourse.entity.CreatePostRequest(content, topicId);

        if (categoryId > 0) {
            request.setCategory(categoryId);
        }

        if (replyToPostNumber != null) {
            request.setReplyToPostNumber(replyToPostNumber);
        }

        DiscourseRetrofitUtil.getInstance()
                .getApiService()
                .createPost(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<retrofit2.Response<CreatePostResponse>>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        observer.OnDisposable(d);
                    }

                    @Override
                    public void onNext(retrofit2.Response<CreatePostResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            observer.OnSuccess(response.body());
                        } else {
                            observer.onError(new ExceptionHelper.ResponseThrowable(
                                    new Exception("发表评论失败: " + response.code()),
                                    ExceptionHelper.ERROR.HTTP_ERROR
                            ));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(ExceptionHelper.handleException(e));
                    }

                    @Override
                    public void onComplete() {
                        observer.OnCompleted();
                    }
                });
    }
}
