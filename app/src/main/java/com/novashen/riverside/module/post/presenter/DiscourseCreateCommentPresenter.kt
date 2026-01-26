package com.novashen.riverside.module.post.presenter

import com.novashen.riverside.api.discourse.entity.CreatePostResponse
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.helper.ExceptionHelper.ResponseThrowable
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.module.post.model.DiscoursePostModel
import com.novashen.riverside.module.post.view.CreateCommentView
import io.reactivex.disposables.Disposable

/**
 * Discourse 发表评论 Presenter
 * 使用 Discourse API 发表评论
 */
class DiscourseCreateCommentPresenter : BaseVBPresenter<CreateCommentView>() {

    private val discoursePostModel = DiscoursePostModel()

    /**
     * 发表评论
     * @param boardId 分类ID（Discourse 的 category_id）
     * @param topicId 帖子ID
     * @param quoteId 引用的评论ID（楼层号）
     * @param isQuote 是否是引用回复
     * @param anonymous 是否匿名（Discourse 不支持，忽略）
     * @param content 评论内容
     * @param imgUrls 图片URL列表（暂不支持）
     * @param imgIds 图片ID列表（暂不支持）
     * @param attachments 附件列表（暂不支持）
     * @param currentReplyUid 当前回复用户ID（忽略）
     */
    fun sendComment(
        boardId: Int,
        topicId: Int,
        quoteId: Int,
        isQuote: Boolean,
        anonymous: Boolean,
        content: String?,
        imgUrls: List<String>?,
        imgIds: List<Int>?,
        attachments: Map<*, *>?,
        currentReplyUid: Int
    ) {
        if (content.isNullOrBlank()) {
            mView?.onSendCommentError("评论内容不能为空")
            return
        }

        // 如果是引用回复，传入楼层号；否则传 null
        val replyToPostNumber = if (isQuote && quoteId > 0) quoteId else null

        discoursePostModel.createPost(
            content,
            topicId,
            boardId,
            replyToPostNumber,
            object : Observer<CreatePostResponse>() {
                override fun OnSuccess(response: CreatePostResponse) {
                    // 转换为原有的 SendPostBean 格式（简化处理）
                    val sendPostBean = com.novashen.riverside.entity.SendPostBean()
                    sendPostBean.rs = com.novashen.riverside.api.ApiConstant.Code.SUCCESS_CODE
                    sendPostBean.body = com.novashen.riverside.entity.SendPostBean.BodyBean()
                    mView?.onSendCommentSuccess(sendPostBean)
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onSendCommentError(e.message ?: "发表评论失败")
                }

                override fun OnCompleted() {
                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    // 图片上传功能暂不支持
    fun uploadImg(files: List<*>?, module: String?, type: String?) {
        mView?.onUploadError("Discourse 暂不支持图片上传，请直接使用图片链接")
    }

    // 图片压缩功能暂不支持
    fun compressImage(files: List<String>) {
        mView?.onCompressImageFail("Discourse 暂不支持图片上传")
    }

    // 附件上传功能暂不支持
    fun uploadAttachment(context: Any?, uid: Int, fid: Int, uri: Any?) {
        mView?.onUploadAttachmentError("Discourse 暂不支持附件上传")
    }

    // 黑名单检查功能暂不支持
    fun checkBlack(tid: Int, fid: Int, quoteId: Int) {
        mView?.onCheckBlack(false)
    }

    // 准备上传附件功能暂不支持
    fun readyUploadAttachment(context: Any?, uri: Any?, fid: Int) {
        mView?.onUploadAttachmentError("Discourse 暂不支持附件上传")
    }

    // 权限请求（保留原有逻辑）
    fun requestPermission(activity: Any?, action: Int, vararg permissions: String?) {
        // 暂不实现
    }
}
