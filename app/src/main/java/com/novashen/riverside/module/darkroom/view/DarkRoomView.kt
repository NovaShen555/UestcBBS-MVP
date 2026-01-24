package com.novashen.riverside.module.darkroom.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.DarkRoomBean

/**
 * Created by sca_tl at 2023/6/6 15:32
 */
interface DarkRoomView: BaseView {
    fun onGetDarkRoomDataSuccess(darkRoomBeanList: List<DarkRoomBean>)
    fun onGetDarkRoomDataError(msg: String?)
}