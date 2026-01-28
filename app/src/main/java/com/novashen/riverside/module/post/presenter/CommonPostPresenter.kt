package com.novashen.riverside.module.post.presenter

import com.novashen.riverside.api.discourse.DiscourseApiHelper
import com.novashen.riverside.api.discourse.DiscourseRetrofitUtil
import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.CommonPostBean
import com.novashen.riverside.helper.ExceptionHelper.ResponseThrowable
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.module.post.model.PostModel
import com.novashen.riverside.module.post.view.CommonPostView
import com.novashen.riverside.util.SharePrefUtil
import io.reactivex.disposables.Disposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by sca_tl at 2023/4/26 10:09
 */
open class CommonPostPresenter: BaseVBPresenter<CommonPostView>() {

    val postModel = PostModel()
    // Helper for Discourse API
    val discourseApiHelper = DiscourseApiHelper(DiscourseRetrofitUtil.getInstance().apiService)
    
    // New simplified method call for Fragment to use
    fun userPostDiscourse(username: String, type: String, page: Int) {
          discourseUserPost(username, type, page)
    }

    fun userPost(page: Int, pageSize: Int, uid: Int, type: String?) {
         val context = mView?.getContext() ?: return // Need context to get username if uid is self or passed
         // Since this project mixes legacy uid and new username, we need to find the username.
         // However, `userPost` signature only has uid.
         // We might need to rely on the Fragment logic that we can fetch `username` if available, or fetch it by uid first.
         // BUT, the View (UserDetailActivity) passes uid and username in Intent.
         // The Fragment (CommonPostFragment) gets it. But `userPost` method here is generic.
         // Let's check how CommonPostFragment calls this. 
         // It calls `presenter.userPost(mPage, Constant.PAGE_SIZE, mUid, mType)`
         
         // HACK: If we can get username from somewhere, use it. But here we only have uid.
         // In UserMainPageFragment, we added username field. CommonPostFragment has mUid.
         // We need to modify CommonPostFragment to pass username to presenter.
         
         // For now, let's assume we can get username if it's "me", or if we modify the signature.
         
         // Wait, the PROMPT says "Please perfect these two tabs". The tabs are "发表" (Post) and "回复" (Reply).
         
         // Let's stick to the existing method signature for now and try to resolve username inside if possible, 
         // OR, better, add a new method `discourseUserPost` and call that from Fragment if type matches.
         
        postModel.getUserPost(uid, page, pageSize, type,
            object : Observer<CommonPostBean>() {
                override fun OnSuccess(userPostBean: CommonPostBean) {
                    if (userPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPostSuccess(userPostBean)
                    }
                    if (userPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetPostError(userPostBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetPostError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }
    
    // New method for Discourse User Posts/Replies
    fun discourseUserPost(username: String, type: String, page: Int) {
         val observable = if (type == "user_post") {
             discourseApiHelper.getUserTopicsAsCommonPost(username)
         } else {
             // user_reply
             discourseApiHelper.getUserActionsAsCommonPost(username, (page - 1) * 30)
         }
         
         observable.subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(object : io.reactivex.Observer<CommonPostBean> {
                 override fun onSubscribe(d: Disposable) {
                     mCompositeDisposable?.add(d)
                 }
                 override fun onNext(bean: CommonPostBean) {
                     mView?.onGetPostSuccess(bean)
                 }
                 override fun onError(e: Throwable) {
                     mView?.onGetPostError(e.message)
                 }
                 override fun onComplete() {}
             })
    }


    fun getHotPostList(page: Int, pageSize: Int) {
        postModel.getHotPost(page, pageSize, 2,
            object : Observer<CommonPostBean>() {
                override fun OnSuccess(hotPostBean: CommonPostBean) {
                    if (hotPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPostSuccess(hotPostBean)
                    }
                    if (hotPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetPostError(hotPostBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetPostError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun getHomeTopicList(page: Int, pageSize: Int, sortby: String?) {
        postModel.getHomeTopicList(page, pageSize, 0, sortby,
            object : Observer<CommonPostBean>() {
                override fun OnSuccess(hotPostBean: CommonPostBean) {
                    if (hotPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPostSuccess(hotPostBean)
                    }
                    if (hotPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetPostError(hotPostBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetPostError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }
}