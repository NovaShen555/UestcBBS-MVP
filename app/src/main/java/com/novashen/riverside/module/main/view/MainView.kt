package com.novashen.riverside.module.main.view

import com.novashen.riverside.base.BaseView
import com.novashen.riverside.entity.SettingsBean
import com.novashen.riverside.entity.UpdateBean

/**
 * Created by sca_tl at 2023/4/11 17:23
 */
interface MainView: BaseView {
    fun getUpdateSuccess(updateBean: UpdateBean)
    fun getSettingsSuccess(settingsBean: SettingsBean)
}