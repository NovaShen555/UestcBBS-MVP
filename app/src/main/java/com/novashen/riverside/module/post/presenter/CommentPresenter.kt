package com.novashen.riverside.module.post.presenter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.novashen.riverside.R
import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.base.BaseVBPresenter
import com.novashen.riverside.entity.AccountBean
import com.novashen.riverside.entity.PostDetailBean
import com.novashen.riverside.helper.ExceptionHelper.ResponseThrowable
import com.novashen.riverside.helper.rxhelper.Observer
import com.novashen.riverside.module.post.model.DiscoursePostModel
import com.novashen.riverside.module.post.model.PostModel
import com.novashen.riverside.module.post.view.CommentView
import com.novashen.riverside.module.report.ReportFragment
import com.novashen.riverside.module.webview.view.WebViewActivity
import com.novashen.riverside.util.Constant
import com.novashen.riverside.util.SharePrefUtil
import com.novashen.riverside.util.TimeUtil
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.litepal.LitePal

/**
 * Created by sca_tl on 2023/1/13 9:37
 */
class CommentPresenter: BaseVBPresenter<CommentView>() {

    private val postModel = PostModel()
    private val discoursePostModel = DiscoursePostModel()

    /**
     * 获取刚刚发送的评论数据
     */
    fun getReplyData(topicId: Int, replyPosition: Int, replyId: Int) {
        val beanList = LitePal.where("uid = $replyId").find(AccountBean::class.java)
        if (beanList.isNullOrEmpty()) {
            return
        }

        val token = beanList[0].token
        val secret = beanList[0].secret

        postModel.getPostDetail(1, 20, 1, topicId, 0, token, secret,
            object : Observer<PostDetailBean>() {
                override fun OnSuccess(postDetailBean: PostDetailBean) {
                    if (postDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetReplyDataSuccess(postDetailBean, replyPosition, replyId)
                    }
                }

                override fun onError(e: ResponseThrowable) { }

                override fun OnCompleted() { }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun getPostComment(page: Int, pageSize: Int, order: Int, topicId: Int, authorId: Int) {
        // 不再请求原有 API，评论数据将通过 EventBus 从 PostDetailBean 中获取
        // 这个方法保留为空，避免调用原有站点的 API
    }

    fun support(tid: Int, pid: Int, type: String, action: String, position: Int) {
        val reactionId = if (action == "support") "+1" else "-1"
        discoursePostModel.toggleReaction(pid, reactionId,
            object : Observer<ResponseBody>() {
                override fun OnSuccess(responseBody: ResponseBody) {
                    mView?.onSupportSuccess(action, position)
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onSupportError(e.message)
                }

                override fun OnCompleted() { }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun stickReply(formHash: String?, fid: Int, tid: Int, stick: Boolean, replyId: Int) {
        postModel.stickReply(formHash, fid, tid, stick, replyId, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                if (s.contains("管理操作成功")) {
                    mView?.onStickReplySuccess(if (stick) "评论置顶成功" else "评论已取消置顶")
                } else if (s.contains("没有权限")) {
                    mView?.onStickReplyError("您没有权限进行此操作，只能操作自己帖子里的评论哦")
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onStickReplyError("操作错误：" + e.message)
            }

            override fun OnCompleted() { }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun checkIfGetAward(tid: Int, pid: Int, commentPosition: Int) {
        postModel.findPost(tid, pid, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)

                    //散水
                    val awardTextSanShui = document.select("div[id=postlist]").select("div[id=post_$pid]")
                        .select("div[class=pct]").select("div[class=cm]").select("h3[class=psth xs1]").text()

                    //大红楼
                    val awardTextRedFloor = document.select("div[id=postlist]").select("div[id=post_$pid]")
                        .select("div[class=pi]").select("div[label=pdbts pdbts_1]").text()

                    if (!awardTextSanShui.isNullOrEmpty()) {
                        mView?.onGetAwardInfoSuccess(awardTextSanShui, commentPosition)
                    } else if (awardTextRedFloor?.contains("checkrush=1") == true) {
                        mView?.onGetAwardInfoSuccess("恭喜，抢中本楼", commentPosition)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(e: ResponseThrowable) {

            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun moreReplyOptionsDialog(fid: Int, tid: Int, authorId: Int, listBean: PostDetailBean.ListBean) {
        val options_view: View = LayoutInflater.from(mView?.getContext()).inflate(R.layout.dialog_post_reply_options, LinearLayout(mView?.getContext()))
        val stick = options_view.findViewById<View>(R.id.options_post_reply_stick)
        val rate = options_view.findViewById<View>(R.id.options_post_reply_rate)
        val report = options_view.findViewById<View>(R.id.options_post_reply_report)
        val onlyAuthor = options_view.findViewById<View>(R.id.options_post_reply_only_author)
        val buchong = options_view.findViewById<View>(R.id.options_post_reply_buchong)
        val delete = options_view.findViewById<View>(R.id.options_post_reply_delete)
        val against = options_view.findViewById<View>(R.id.options_post_reply_against)
        val modify = options_view.findViewById<View>(R.id.options_post_reply_modify)
        val dianping = options_view.findViewById<View>(R.id.options_post_reply_dianping)
        val stickText = options_view.findViewById<TextView>(R.id.options_post_reply_stick_text)
        stickText.text = if (listBean.poststick == 0) "置顶" else "取消置顶"
        buchong.visibility = if (listBean.reply_id == SharePrefUtil.getUid(mView?.getContext())) View.VISIBLE else View.GONE
        rate.visibility = if (listBean.reply_id == SharePrefUtil.getUid(mView?.getContext())) View.GONE else View.VISIBLE
        delete.visibility = if (listBean.reply_id == SharePrefUtil.getUid(mView?.getContext())) View.VISIBLE else View.GONE
        stick.visibility = if (authorId == SharePrefUtil.getUid(mView?.getContext())) View.VISIBLE else View.GONE
        modify.visibility = if (listBean.reply_id == SharePrefUtil.getUid(mView?.getContext())) View.VISIBLE else View.GONE
        against.visibility = if (listBean.reply_id == SharePrefUtil.getUid(mView?.getContext())) View.GONE else View.VISIBLE
        report.visibility = if (listBean.reply_id == SharePrefUtil.getUid(mView?.getContext())) View.GONE else View.VISIBLE
        val options_dialog = MaterialAlertDialogBuilder(options_view.context)
            .setView(options_view)
            .create()
        options_dialog.show()
        stick.setOnClickListener { v: View? ->
            stickReply(
                SharePrefUtil.getForumHash(mView?.getContext()),
                fid,
                tid,
                listBean.poststick == 0,
                listBean.reply_posts_id
            )
            options_dialog.dismiss()
        }
        rate.setOnClickListener { v: View? ->
            mView?.onPingFen(listBean.reply_posts_id)
            options_dialog.dismiss()
        }
        onlyAuthor.setOnClickListener { v: View? ->
            mView?.onOnlyReplyAuthor(listBean.reply_id)
            options_dialog.dismiss()
        }
        report.setOnClickListener { v: View? ->
            val bundle = Bundle()
            bundle.putString(Constant.IntentKey.TYPE, "post")
            bundle.putInt(Constant.IntentKey.ID, listBean.reply_posts_id)
            if (mView?.getContext() is FragmentActivity) {
                ReportFragment.getInstance(bundle).show(
                    (mView?.getContext() as FragmentActivity).supportFragmentManager,
                    TimeUtil.getStringMs()
                )
            }
            options_dialog.dismiss()
        }
        buchong.setOnClickListener { v: View? ->
            mView?.onAppendPost(listBean.reply_posts_id, tid)
            options_dialog.dismiss()
        }
        delete.setOnClickListener { v: View? ->
            mView?.onDeletePost(tid, listBean.reply_posts_id)
            options_dialog.dismiss()
        }
        against.setOnClickListener { v: View? ->
            support(tid, listBean.reply_posts_id, "post", "against", 0)
            options_dialog.dismiss()
        }
        dianping.setOnClickListener { v: View? ->
            mView?.onDianPing(listBean.reply_posts_id)
            options_dialog.dismiss()
        }
        modify.setOnClickListener { v: View? ->
            val intent = Intent(mView?.getContext(), WebViewActivity::class.java)
            intent.putExtra(
                Constant.IntentKey.URL,
                "https://bbs.uestc.edu.cn/forum.php?mod=post&action=edit&tid=" + tid + "&pid=" + listBean.reply_posts_id
            )
            mView?.getContext()?.startActivity(intent)
        }
    }

}