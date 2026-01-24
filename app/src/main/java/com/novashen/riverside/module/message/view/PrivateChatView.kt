package com.novashen.riverside.module.message.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.PrivateChatBean
import com.novashen.riverside.entity.SendPrivateMsgResultBean
import com.novashen.riverside.entity.UploadResultBean
import java.io.File

/**
 * Created by sca_tl at 2023/3/29 15:35
 */
interface PrivateChatView: BaseView {
    fun onGetPrivateListSuccess(privateChatBean: PrivateChatBean)
    fun onGetPrivateListError(msg: String?)
    fun onSendPrivateChatMsgSuccess(sendPrivateMsgResultBean: SendPrivateMsgResultBean, content: String?, type: String)
    fun onSendPrivateChatMsgError(msg: String?)
    fun onCompressImageSuccess(compressedFiles: List<File>)
    fun onCompressImageFail(msg: String?)
    fun onUploadSuccess(uploadResultBean: UploadResultBean)
    fun onUploadError(msg: String?)
    fun onDeleteSinglePmSuccess(msg: String?, position: Int)
    fun onDeleteSinglePmError(msg: String?)
    fun onGetUserSpaceSuccess(isOnline: Boolean, )
    fun onGetUserSpaceError(msg: String?)
}