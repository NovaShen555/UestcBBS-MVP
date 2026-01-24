package com.novashen.riverside.module.collection.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.CollectionDetailBean

/**
 * Created by sca_tl at 2023/5/4 14:18
 */
interface CollectionDetailView: BaseView {
    fun onGetCollectionSuccess(collectionDetailBean: CollectionDetailBean, hasNext: Boolean)
    fun onGetCollectionError(msg: String?)
    fun onSubscribeCollectionSuccess(subscribe: Boolean)
    fun onSubscribeCollectionError(msg: String?)
    fun onDeleteCollectionPostSuccess(msg: String?, position: Int)
    fun onDeleteCollectionPostError(msg: String?)
    fun onDeleteCollectionSuccess(msg: String?)
    fun onDeleteCollectionError(msg: String?)
}