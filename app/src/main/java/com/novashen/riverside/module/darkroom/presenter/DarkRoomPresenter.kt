package com.novashen.riverside.module.darkroom.presenter

import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.DarkRoomBean
import com.novashen.riverside.http.Observer
import com.novashen.riverside.module.darkroom.model.DarkRoomModel
import com.novashen.riverside.module.darkroom.view.DarkRoomView
import com.novashen.riverside.util.BBSLinkUtil.getLinkInfo
import com.novashen.riverside.util.subscribeEx
import org.jsoup.Jsoup

/**
 * Created by sca_tl at 2023/6/6 15:33
 */
class DarkRoomPresenter: BaseVBPresenter<DarkRoomView>() {
    private val darkRoomModel = DarkRoomModel()

    fun getDarkRoomList() {
        darkRoomModel
            .getDarkRoomList()
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    try {
                        val document = Jsoup.parse(it)
                        val elements = document.select("table[id=darkroomtable]").select("tbody").select("tr")
                        val darkRoomBeanList: MutableList<DarkRoomBean> = ArrayList()
                        for (i in 1 until elements.size) {
                            val darkRoomBean = DarkRoomBean()
                            darkRoomBean.username = elements[i].select("td")[0].select("a").text()
                            darkRoomBean.uid = getLinkInfo(elements[i].select("td")[0].select("a").attr("href")).id
                            darkRoomBean.action = elements[i].select("td")[1].text()
                            darkRoomBean.dateline = elements[i].select("td")[2].text()
                            darkRoomBean.actionTime = elements[i].select("td")[3].text()
                            darkRoomBean.reason = elements[i].select("td")[4].text()
                            darkRoomBeanList.add(darkRoomBean)
                        }
                        mView?.onGetDarkRoomDataSuccess(darkRoomBeanList)
                    } catch (e: Exception) {
                        mView?.onGetDarkRoomDataError("获取小黑屋数据失败：" + e.message)
                        e.printStackTrace()
                    }
                }

                onError {
                    mView?.onGetDarkRoomDataError("获取小黑屋数据失败：${it.message}")
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }
}