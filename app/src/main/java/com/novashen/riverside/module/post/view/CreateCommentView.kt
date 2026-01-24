package com.novashen.riverside.module.post.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.AttachmentBean
import com.novashen.riverside.entity.SendPostBean
import com.novashen.riverside.entity.UploadResultBean
import java.io.File

/**
 * Created by sca_tl at 2023/4/17 9:37
 */
interface CreateCommentView: BaseView {
    fun onSendCommentSuccess(sendPostBean: SendPostBean)
    fun onSendCommentError(msg: String?)
    fun onUploadSuccess(uploadResultBean: UploadResultBean)
    fun onUploadError(msg: String?)
    fun onCompressImageSuccess(compressedFiles: List<File>)
    fun onCompressImageFail(msg: String?)
    fun onPermissionGranted(action: Int)
    fun onPermissionRefused()
    fun onPermissionRefusedWithNoMoreRequest()
    fun onStartUploadAttachment()
    fun onUploadAttachmentSuccess(attachmentBean: AttachmentBean, msg: String?)
    fun onUploadAttachmentError(msg: String?)
    fun onCheckBlack(blacked: Boolean)
}