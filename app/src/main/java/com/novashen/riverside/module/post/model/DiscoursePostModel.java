package com.novashen.riverside.module.post.model;

import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil;
import com.novashen.riverside.api.discourse.converter.DiscoursePostDetailConverter;
import com.novashen.riverside.api.discourse.entity.CreatePostResponse;
import com.novashen.riverside.api.discourse.entity.TopicDetailResponse;
import com.novashen.riverside.entity.PostDetailBean;
import com.novashen.riverside.helper.ExceptionHelper;
import com.novashen.riverside.helper.rxhelper.Observer;
import okhttp3.ResponseBody;

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
                .map(response -> {
                android.util.Log.d("DiscoursePostModel", "Fetched topic detail, topicId=" + topicId
                    + ", posts=" + (response.postStream != null && response.postStream.posts != null ? response.postStream.posts.size() : 0));
                    // 将 Discourse API 响应转换为 PostDetailBean
                PostDetailBean bean = DiscoursePostDetailConverter.convert(response);
                android.util.Log.d("DiscoursePostModel", "Converted PostDetailBean, listSize="
                    + (bean.list != null ? bean.list.size() : 0)
                    + ", supportCount=" + (bean.postWebBean != null ? bean.postWebBean.supportCount : -1)
                    + ", againstCount=" + (bean.postWebBean != null ? bean.postWebBean.againstCount : -1));
                return bean;
                })
                .observeOn(AndroidSchedulers.mainThread())
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
     * 创建新帖子
     * @param title 帖子标题
     * @param content 帖子内容
     * @param categoryId 板块ID
     * @param observer 观察者
     */
    public void createNewTopic(String title, String content, int categoryId, Observer<CreatePostResponse> observer) {
        // 创建请求体
        com.novashen.riverside.api.discourse.entity.CreatePostRequest request =
                new com.novashen.riverside.api.discourse.entity.CreatePostRequest(title, content, categoryId);

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
                            // 尝试从错误响应体中提取详细错误信息
                            StringBuilder errorMessage = new StringBuilder();
                            errorMessage.append("发表帖子失败:\n");
                            errorMessage.append("HTTP Code: ").append(response.code()).append("\n");
                            errorMessage.append("URL: ").append(response.raw().request().url()).append("\n");

                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    errorMessage.append("Response Body: ").append(errorBody).append("\n");
                                }

                                // 打印请求头信息
                                errorMessage.append("Request Headers:\n");
                                errorMessage.append(response.raw().request().headers().toString()).append("\n");

                            } catch (Exception e) {
                                errorMessage.append("Error reading response: ").append(e.getMessage()).append("\n");
                                errorMessage.append("Stack trace:\n");
                                for (StackTraceElement element : e.getStackTrace()) {
                                    errorMessage.append("  ").append(element.toString()).append("\n");
                                }
                            }

                            observer.onError(new ExceptionHelper.ResponseThrowable(
                                    new Exception(errorMessage.toString()),
                                    ExceptionHelper.ERROR.HTTP_ERROR
                            ));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 构建详细的错误信息，包含堆栈跟踪
                        StringBuilder errorMessage = new StringBuilder();
                        errorMessage.append("发表帖子失败 (onError):\n");
                        errorMessage.append("Exception: ").append(e.getClass().getName()).append("\n");
                        errorMessage.append("Message: ").append(e.getMessage()).append("\n");
                        errorMessage.append("Stack trace:\n");
                        for (StackTraceElement element : e.getStackTrace()) {
                            errorMessage.append("  ").append(element.toString()).append("\n");
                        }

                        if (e.getCause() != null) {
                            errorMessage.append("Caused by: ").append(e.getCause().getClass().getName()).append("\n");
                            errorMessage.append("Cause message: ").append(e.getCause().getMessage()).append("\n");
                        }

                        observer.onError(new ExceptionHelper.ResponseThrowable(
                                new Exception(errorMessage.toString()),
                                ExceptionHelper.ERROR.UNKNOWN
                        ));
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
                            // 尝试从错误响应体中提取详细错误信息
                            StringBuilder errorMessage = new StringBuilder();
                            errorMessage.append("发表评论失败:\n");
                            errorMessage.append("HTTP Code: ").append(response.code()).append("\n");
                            errorMessage.append("URL: ").append(response.raw().request().url()).append("\n");

                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    errorMessage.append("Response Body: ").append(errorBody).append("\n");
                                }

                                // 打印请求头信息
                                errorMessage.append("Request Headers:\n");
                                errorMessage.append(response.raw().request().headers().toString()).append("\n");

                            } catch (Exception e) {
                                errorMessage.append("Error reading response: ").append(e.getMessage()).append("\n");
                                errorMessage.append("Stack trace:\n");
                                for (StackTraceElement element : e.getStackTrace()) {
                                    errorMessage.append("  ").append(element.toString()).append("\n");
                                }
                            }

                            observer.onError(new ExceptionHelper.ResponseThrowable(
                                    new Exception(errorMessage.toString()),
                                    ExceptionHelper.ERROR.HTTP_ERROR
                            ));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 构建详细的错误信息，包含堆栈跟踪
                        StringBuilder errorMessage = new StringBuilder();
                        errorMessage.append("发表评论失败 (onError):\n");
                        errorMessage.append("Exception: ").append(e.getClass().getName()).append("\n");
                        errorMessage.append("Message: ").append(e.getMessage()).append("\n");
                        errorMessage.append("Stack trace:\n");
                        for (StackTraceElement element : e.getStackTrace()) {
                            errorMessage.append("  ").append(element.toString()).append("\n");
                        }

                        if (e.getCause() != null) {
                            errorMessage.append("Caused by: ").append(e.getCause().getClass().getName()).append("\n");
                            errorMessage.append("Cause message: ").append(e.getCause().getMessage()).append("\n");
                        }

                        observer.onError(new ExceptionHelper.ResponseThrowable(
                                new Exception(errorMessage.toString()),
                                ExceptionHelper.ERROR.UNKNOWN
                        ));
                    }

                    @Override
                    public void onComplete() {
                        observer.OnCompleted();
                    }
                });
    }

    /**
     * 切换反应（点赞/点踩）
     */
    public void toggleReaction(int postId, String reactionId, Observer<ResponseBody> observer) {
        DiscourseRetrofitUtil.getInstance()
                .getApiService()
                .toggleReaction(postId, reactionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void createBookmark(int postId, Observer<ResponseBody> observer) {
        DiscourseRetrofitUtil.getInstance()
                .getApiService()
                .createBookmark("", 3, postId, "Post")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void deleteBookmark(int bookmarkId, Observer<ResponseBody> observer) {
        DiscourseRetrofitUtil.getInstance()
                .getApiService()
                .deleteBookmark(bookmarkId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
