package com.novashen.riverside.module.search.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.SearchPostBean
import com.novashen.riverside.entity.SearchUserBean

/**
 * Created by sca_tl at 2023/2/8 10:22
 */
interface SearchView: BaseView {
    fun onSearchUserSuccess(searchUserBean: SearchUserBean)
    fun onSearchUserError(msg: String?)
    fun onSearchPostSuccess(searchPostBean: SearchPostBean)
    fun onSearchPostError(msg: String?)
}