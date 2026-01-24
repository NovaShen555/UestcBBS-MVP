package com.novashen.riverside.module.post.presenter

import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.PostDetailBean
import com.novashen.riverside.helper.ExceptionHelper.ResponseThrowable
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.module.post.model.PostModel
import com.novashen.riverside.module.post.view.DianZanView
import com.novashen.riverside.util.SharePrefUtil
import io.reactivex.disposables.Disposable

/**
 * Created by sca_tl at 2023/4/13 14:09
 */
class DianZanPresenter: BaseVBPresenter<DianZanView>() {

    private val postModel = PostModel()

    fun getPostDetail(page: Int, pageSize: Int, order: Int, topicId: Int, authorId: Int) {
        postModel.getPostDetail(page, pageSize, order, topicId, authorId,
            SharePrefUtil.getToken(mView?.getContext()),
            SharePrefUtil.getSecret(mView?.getContext()),
            object : Observer<PostDetailBean>() {
                override fun OnSuccess(postDetailBean: PostDetailBean) {
                    if (postDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPostDetailSuccess(postDetailBean)
                    }
                    if (postDetailBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetPostDetailError(postDetailBean.head.errInfo)
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

}