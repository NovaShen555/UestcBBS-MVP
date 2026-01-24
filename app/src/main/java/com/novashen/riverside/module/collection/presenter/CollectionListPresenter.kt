package com.novashen.riverside.module.collection.presenter

import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.helper.ExceptionHelper.ResponseThrowable
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.module.collection.model.CollectionModel
import com.novashen.riverside.module.collection.view.CollectionListView
import com.novashen.riverside.util.JsoupParseUtil
import io.reactivex.disposables.Disposable

/**
 * Created by sca_tl at 2023/5/5 11:43
 */
class CollectionListPresenter: BaseVBPresenter<CollectionListView>() {

    private val collectionModel = CollectionModel()

    fun getCollectionList(page: Int, op: String, order: String) {
        collectionModel.getCollectionList(page, op, order, object : Observer<String>() {
            override fun OnSuccess(html: String) {
                val collectionBeans = JsoupParseUtil.parseCollectionList(html)
                mView?.onGetCollectionListSuccess(collectionBeans, html.contains("下一页"))
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetCollectionListError("获取数据失败" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}