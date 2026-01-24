package com.novashen.riverside.module.collection.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.CollectionListBean

/**
 * Created by sca_tl at 2023/5/5 11:43
 */
interface CollectionListView: BaseView {
    fun onGetCollectionListSuccess(collectionListBeans: List<CollectionListBean>, hasNext: Boolean)
    fun onGetCollectionListError(msg: String?)
}