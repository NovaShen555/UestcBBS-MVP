package com.novashen.riverside.module.post.presenter

import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.api.discourse.entity.CreatePostResponse
import com.novashen.riverside.entity.PostDetailBean
import com.novashen.riverside.helper.ExceptionHelper.ResponseThrowable
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.module.post.model.DiscoursePostModel
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import org.json.JSONObject

/**
 * Discourse 帖子详情 Presenter
 * 使用 Discourse API 获取帖子详情
 */
class DiscoursePostDetailPresenter : NewPostDetailPresenter() {

    private val discoursePostModel = DiscoursePostModel()
    private var currentCategoryId = 0  // 保存当前帖子的分类ID

    /**
     * 获取帖子详情
     * @param page 页码（Discourse API 不支持分页，此参数忽略）
     * @param pageSize 每页大小（Discourse API 不支持分页，此参数忽略）
     * @param order 排序方式（Discourse API 不支持，此参数忽略）
     * @param topicId 帖子ID
     * @param authorId 作者ID（Discourse API 不支持，此参数忽略）
     */
    override fun getDetail(page: Int, pageSize: Int, order: Int, topicId: Int, authorId: Int) {
        discoursePostModel.getPostDetail(topicId, object : Observer<PostDetailBean>() {
            override fun OnSuccess(postDetailBean: PostDetailBean) {
                if (postDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    // 保存分类ID，用于发表评论
                    currentCategoryId = postDetailBean.boardId
                    mView?.onGetPostDetailSuccess(postDetailBean)
                } else {
                    mView?.onGetPostDetailError("获取帖子详情失败")
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetPostDetailError(e.message)
            }

            override fun OnCompleted() {
            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    /**
     * 发表评论
     * @param content 评论内容
     * @param topicId 帖子ID
     * @param replyToPostNumber 回复的楼层号（可选）
     */
    fun sendComment(content: String, topicId: Int, replyToPostNumber: Int?) {
        // 转换表情格式：将 [字母:数字] 转换为 :字母数字:
        // 例如: [a:1168] -> :a1168:, [s:123] -> :s123:
        val convertedContent = convertEmojiFormat(content)

        discoursePostModel.createPost(convertedContent, topicId, currentCategoryId, replyToPostNumber,
            object : Observer<CreatePostResponse>() {
                override fun OnSuccess(response: CreatePostResponse) {
                    if (response.success) {
                        // 评论成功，重新加载帖子详情
                        getDetail(1, 0, 0, topicId, 0)
                    } else {
                        mView?.onGetPostDetailError("发表评论失败")
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetPostDetailError(e.message)
                }

                override fun OnCompleted() {
                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    /**
     * 转换表情格式
     * 将 [字母:数字] 格式转换为 :字母数字: 格式（Discourse 标准表情格式）
     * 特殊处理：将字母 'a' 转换为 's'
     * 例如: [a:1168] -> :s1168:, [s:123] -> :s123:
     */
    private fun convertEmojiFormat(text: String): String {
        return text.replace(Regex("\\[([a-zA-Z]+):(\\d+)\\]")) { matchResult ->
            val letter = matchResult.groupValues[1]
            val number = matchResult.groupValues[2]
            val convertedLetter = if (letter == "a") "s" else letter
            ":$convertedLetter$number:"
        }
    }

    override fun support(tid: Int, pid: Int, type: String, action: String) {
        val reactionId = if (action == "support") "+1" else "-1"
        discoursePostModel.toggleReaction(pid, reactionId, object : Observer<ResponseBody>() {
            override fun OnSuccess(responseBody: ResponseBody) {
                mView?.onSupportSuccess(action, reactionId)
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onSupportError(e.message)
            }

            override fun OnCompleted() {}

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    override fun bookmark(postId: Int, bookmarked: Boolean, bookmarkId: Int) {
        if (bookmarked) {
            if (bookmarkId <= 0) {
                mView?.onBookmarkError("未找到书签ID")
                return
            }
            discoursePostModel.deleteBookmark(bookmarkId, object : Observer<ResponseBody>() {
                override fun OnSuccess(responseBody: ResponseBody) {
                    mView?.onBookmarkSuccess(false, 0)
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onBookmarkError(e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
        } else {
            discoursePostModel.createBookmark(postId, object : Observer<ResponseBody>() {
                override fun OnSuccess(responseBody: ResponseBody) {
                    var id = 0
                    try {
                        val json = JSONObject(responseBody.string())
                        id = json.optInt("id", 0)
                    } catch (_: Exception) { }
                    mView?.onBookmarkSuccess(true, id)
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onBookmarkError(e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
        }
    }
}
