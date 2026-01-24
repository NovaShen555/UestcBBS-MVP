package com.novashen.riverside.module.main.presenter

import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.SettingsBean
import com.novashen.riverside.entity.UpdateBean
import com.novashen.riverside.module.main.model.MainModel
import com.novashen.riverside.module.main.view.MainView
import com.novashen.riverside.util.subscribeEx

/**
 * Created by sca_tl at 2023/4/11 17:23
 */
class MainPresenter: BaseVBPresenter<MainView>() {

    private val mainModel = MainModel()

    fun getUpdate(oldVersionCode: Int, isTest: Boolean) {
        mainModel
            .getUpdate(oldVersionCode, isTest)
            .subscribeEx(com.novashen.riverside.http.Observer<UpdateBean>().observer {
                onSuccess {
                    mView?.getUpdateSuccess(it)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

    fun getSettings() {
        mainModel
            .getSettings()
            .subscribeEx(com.novashen.riverside.http.Observer<SettingsBean>().observer {
                onSuccess {
                    mView?.getSettingsSuccess(it)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

}