package com.novashen.riverside.module.account.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.just.agentweb.AgentWebConfig
import com.novashen.riverside.R
import com.novashen.riverside.annotation.ResetPswType
import com.novashen.riverside.annotation.ToastType
import com.novashen.riverside.api.ApiConstant
import com.novashen.riverside.base.BaseEvent
import com.novashen.riverside.base.BaseVBActivity
import com.novashen.riverside.databinding.ActivityAccountManageBinding
import com.novashen.riverside.entity.AccountBean
import com.novashen.riverside.entity.LoginBean
import com.novashen.riverside.manager.BlackListManager
import com.novashen.riverside.manager.MessageManager
import com.novashen.riverside.module.account.adapter.AccountManageAdapter
import com.novashen.riverside.module.account.presenter.AccountManagePresenter
import com.novashen.riverside.services.HeartMsgService
import com.novashen.riverside.util.CommonUtil
import com.novashen.riverside.util.Constant
import com.novashen.riverside.util.SharePrefUtil
import com.novashen.riverside.util.TimeUtil
import com.novashen.riverside.util.showToast
import com.novashen.util.ServiceUtil.isServiceRunning
import com.novashen.util.startServiceCompat
import com.novashen.widget.dialog.BlurAlertDialogBuilder
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal

/**
 * Created by sca_tl at 2023/6/2 9:42
 */
class AccountManageActivity: BaseVBActivity<AccountManagePresenter, AccountManageView, ActivityAccountManageBinding>(), AccountManageView {

    private lateinit var accountManageAdapter: AccountManageAdapter

    override fun getViewBinding() = ActivityAccountManageBinding.inflate(layoutInflater)

    override fun initPresenter() = AccountManagePresenter()

    override fun initView(theftProof: Boolean) {
        super.initView(true)
        accountManageAdapter = AccountManageAdapter(R.layout.item_account_manage)
        mBinding.recyclerView.adapter = accountManageAdapter

        val data = LitePal.findAll(AccountBean::class.java)
        accountManageAdapter.currentLoginUid = SharePrefUtil.getUid(this)
        accountManageAdapter.setNewData(data)
        if (data.size == 0) {
            mBinding.statusView.empty("点击右上角添加帐号")
        } else {
            mBinding.statusView.success()
        }
    }

    override fun setOnItemClickListener() {
        accountManageAdapter.setOnItemClickListener { adapter, view, position ->
            val accountBean = AccountBean().apply {
                isLogin = true
                avatar = accountManageAdapter.data[position].avatar
                secret = accountManageAdapter.data[position].secret
                token = accountManageAdapter.data[position].token
                uid = accountManageAdapter.data[position].uid
                userName = accountManageAdapter.data[position].userName
            }
            accountBean.saveOrUpdate("uid = ?", accountBean.uid.toString())
            SharePrefUtil.setLogin(this, true, accountBean)
            EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.LOGIN_SUCCESS))

            accountManageAdapter.currentLoginUid = accountBean.uid
            accountManageAdapter.notifyItemRangeChanged(0, accountManageAdapter.data.size)

            //开启消息提醒服务
            if (!isServiceRunning(this, HeartMsgService.SERVICE_NAME)) {
                val intent = Intent(this, HeartMsgService::class.java)
                startServiceCompat(intent)
            }

            for (cookie in SharePrefUtil.getCookies(this, accountBean.userName)) {
                AgentWebConfig.syncCookie(ApiConstant.BBS_BASE_URL, cookie)
            }

            mPresenter?.getUploadHash(1430861, false)
            MessageManager.INSTANCE.resetCount()
            BlackListManager.INSTANCE.init()
            showToast("欢迎回来，" + accountBean.userName, ToastType.TYPE_SUCCESS)
        }

        accountManageAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.delete_btn) {
                showDeleteAccountDialog(position)
            }
            if (view.id == R.id.real_name) {
                showToast("查询中，请稍候...", ToastType.TYPE_NORMAL)
                mPresenter?.getRealNameInfo()
            }
        }
    }

    private fun showDeleteAccountDialog(position: Int) {
        val accountBean = accountManageAdapter.data[position]
        val msg1 = "确认要删除帐号： ${accountBean.userName} 吗？由于该帐号当前已登录，删除后会退出登录该账号"
        val msg2 = "确认要删除帐号： ${accountBean.userName} 吗？"
        BlurAlertDialogBuilder(this)
            .setNegativeButton("取消", null)
            .setPositiveButton("确认") { dialog, which ->
                if (LitePal.delete(AccountBean::class.java, accountBean.id.toLong()) != 0) {
                    MessageManager.INSTANCE.resetCount()
                    SharePrefUtil.setCookies(this, HashSet(), accountBean.userName)
                    SharePrefUtil.setSuperAccount(this, false, accountBean.userName)
                    SharePrefUtil.setUploadHash(this, "", accountBean.userName)

                    if (accountBean.uid == SharePrefUtil.getUid(this)) {
                        SharePrefUtil.setLogin(this, false, AccountBean())
                    }

                    accountManageAdapter.data.removeAt(position)
                    accountManageAdapter.notifyItemRemoved(position)

                    if (accountManageAdapter.data.size == 0) {
                        mBinding.statusView.empty("点击右上角添加帐号")
                    } else {
                        mBinding.statusView.success()
                    }

                    EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.LOGOUT_SUCCESS))
                    showToast("删除成功", ToastType.TYPE_SUCCESS)
                } else {
                    showToast("删除失败，未找到该帐号", ToastType.TYPE_ERROR)
                }
                dialog.dismiss()
            }
            .setTitle("删除帐号")
            .setMessage(if (accountBean.uid == SharePrefUtil.getUid(this)) msg1 else msg2)
            .create()
            .show()
    }

    override fun onOptionsSelected(item: MenuItem?) {
        if (item?.itemId == R.id.menu_account_manager_add_account) {
            LoginFragment.getInstance(null).show(supportFragmentManager, TimeUtil.getStringMs())
        }

        if (item?.itemId == R.id.menu_account_manager_reset_psw) {
            val bundle = Bundle().apply {
                putString(Constant.IntentKey.TYPE, ResetPswType.TYPE_RESET)
            }
            ResetPasswordFragment.getInstance(bundle).show(supportFragmentManager, TimeUtil.getStringMs())
        }

        if (item?.itemId == R.id.menu_account_manager_find_username) {
            val bundle = Bundle().apply {
                putString(Constant.IntentKey.TYPE, ResetPswType.TYPE_FIND)
            }
            ResetPasswordFragment.getInstance(bundle).show(supportFragmentManager, TimeUtil.getStringMs())
        }

        if (item?.itemId == R.id.menu_account_manager_register_account) {
            CommonUtil.openBrowser(this, Constant.REGISTER_URL)
        }

        if (item?.itemId == R.id.menu_account_manager_get_upload_hash) {
            if (!SharePrefUtil.isLogin(this)) {
                showToast("请先登录", ToastType.TYPE_WARNING)
            } else {
                mPresenter?.getUploadHash(1430861, true)
            }
        }
    }

    override fun onGetRealNameInfoSuccess(info: String?) {
        showToast(info, ToastType.TYPE_SUCCESS)
    }

    override fun onGetRealNameInfoError(msg: String?) {
        showToast(msg, ToastType.TYPE_SUCCESS)
    }

    override fun onGetUploadHashSuccess(hash: String?, msg: String?, toast: Boolean) {
        if (toast) {
            showToast(msg, ToastType.TYPE_SUCCESS)
        }
    }

    override fun onGetUploadHashError(msg: String?, toast: Boolean) {
        if (toast) {
            showToast(msg, ToastType.TYPE_ERROR)
        }
    }

    override fun getContext() = this

    override fun registerEventBus() = true

    override fun onEventBusReceived(baseEvent: BaseEvent<Any>?) {
        if (baseEvent?.eventCode == BaseEvent.EventCode.ADD_ACCOUNT_SUCCESS) {
            val loginBean = baseEvent.eventData as LoginBean
            val accountBean = AccountBean().apply {
                isLogin = false
                avatar = loginBean.avatar
                secret = loginBean.secret
                token = loginBean.token
                uid = loginBean.uid
                userName = loginBean.userName
            }

            if (!LitePal.findAll(AccountBean::class.java).contains(accountBean)) {
                accountBean.save()
                accountManageAdapter.addData(accountBean)
                accountManageAdapter.notifyItemInserted(accountManageAdapter.data.size)
                mBinding.statusView.success()
                showToast("添加帐号成功，请点击登录", ToastType.TYPE_SUCCESS)
            } else {
                showToast("已有该帐号，点击即可登录", ToastType.TYPE_NORMAL)
            }
        }
    }
}