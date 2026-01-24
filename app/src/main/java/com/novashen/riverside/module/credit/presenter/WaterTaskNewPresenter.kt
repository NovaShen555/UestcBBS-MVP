package com.novashen.riverside.module.credit.presenter

import com.novashen.riverside.App
import com.novashen.riverside.annotation.TaskType
import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.TaskBean
import com.novashen.riverside.helper.ExceptionHelper.ResponseThrowable
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.module.credit.model.CreditModel
import com.novashen.riverside.module.credit.view.WaterTaskNewView
import com.novashen.riverside.util.BBSLinkUtil
import com.novashen.riverside.util.SharePrefUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * Created by sca_tl at 2023/4/7 18:01
 */
open class WaterTaskNewPresenter<T: WaterTaskNewView>: BaseVBPresenter<T>() {

    private val creditModel = CreditModel()

    fun getNewTaskList() {
        creditModel.getNewTask(object : Observer<String>() {
            override fun OnSuccess(s: String) {
                if (s.contains("需要先登录")) {
                    mView?.onGetNewTaskError("请获取Cookies后进行此操作")
                } else {
                    try {
                        val document = Jsoup.parse(s)
                        val elements = document.select("div[class=ct2_a wp cl]").select("div[class=ptm]").select("table").select("tbody").select("tr")
                        val formhash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                        SharePrefUtil.setForumHash(App.getContext(), formhash)
                        val taskBeans: MutableList<TaskBean> = ArrayList()
                        for (i in elements.indices) {
                            val taskBean = TaskBean()
                            taskBean.type = TaskType.TYPE_NEW
                            taskBean.name = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].text()
                            taskBean.id = BBSLinkUtil.getLinkInfo(elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].attr("href")).id
                            taskBean.popularNum = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("span[class=xs1 xg2 xw0]").select("a").text().toInt()
                            taskBean.dsp = elements[i].select("td[class=bbda ptm pbm]").select("p[class=xg2]").text()
                            taskBean.award = elements[i].select("td[class=xi1 bbda hm]").text()
                            taskBean.icon = ApiConstant.BBS_BASE_URL + elements[i].select("td")[0].select("img").attr("src")
                            taskBeans.add(taskBean)
                        }
                        mView?.onGetNewTaskSuccess(taskBeans, formhash)
                    } catch (e: Exception) {
                        mView?.onGetNewTaskError("获取任务失败：" + e.message)
                    }
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetNewTaskError("获取任务失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun applyNewTask(id: Int, position: Int) {
        creditModel.applyNewTask(id, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val msg = document.select("div[id=messagetext]").text()
                    mView?.onApplyNewTaskSuccess(msg, id, position)
                } catch (e: Exception) {
                    mView?.onApplyNewTaskError("申请任务失败：" + e.message)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onApplyNewTaskError("申请任务失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}