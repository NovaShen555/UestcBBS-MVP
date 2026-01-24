package com.novashen.riverside.module.setting.presenter

import android.app.Activity
import com.alibaba.fastjson.JSONObject
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.OpenSourceBean
import com.novashen.riverside.module.setting.view.OpenSourceView
import com.novashen.util.FileUtil
import kotlin.concurrent.thread

/**
 * Created by sca_tl at 2023/6/6 16:17
 */
class OpenSourcePresenter: BaseVBPresenter<OpenSourceView>() {

    fun getOpenSourceData() {
        thread {
            val data = FileUtil.readAssetFileToString(mView?.getContext(), "open_source_projects.json")
            try {
                val openSourceBeanList = JSONObject.parseArray(data, OpenSourceBean::class.java)
                (mView?.getContext() as Activity).runOnUiThread {
                    mView?.onGetOpenSourceDataSuccess(openSourceBeanList)
                }
            } catch (e: Exception) {
                (mView?.getContext() as Activity).runOnUiThread {
                    mView?.onGetOpenSourceDataError(e.message)
                }
            }
        }
    }

}