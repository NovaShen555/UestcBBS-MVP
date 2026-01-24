package com.novashen.riverside.module.search.model

import com.novashen.riverside.entity.SearchPostBean
import com.novashen.riverside.entity.SearchUserBean
import com.novashen.riverside.util.RetrofitUtil
import io.reactivex.Observable

/**
 * Created by sca_tl at 2023/4/4 9:52
 */
class SearchModel {
    fun searchUser(page: Int,
                   pageSize: Int,
                   searchId: Int,
                   keyword: String?): Observable<SearchUserBean> =
        RetrofitUtil
            .getInstance()
            .apiService
            .searchUser(page, pageSize, searchId, keyword)

    fun searchPost(page: Int,
                   pageSize: Int,
                   keyword: String?): Observable<SearchPostBean> =
        RetrofitUtil
            .getInstance()
            .apiService
            .searchPost(page, pageSize, keyword)

}