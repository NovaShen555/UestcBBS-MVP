package com.novashen.riverside.module.webview.view

import android.content.Intent
import android.view.KeyEvent
import android.view.MenuItem
import android.webkit.WebView
import android.widget.RelativeLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient
import com.novashen.riverside.R
import com.novashen.riverside.base.BaseVBActivity
import com.novashen.riverside.databinding.ActivityWebViewBinding
import com.novashen.riverside.module.webview.presenter.WebViewPresenter
import com.novashen.riverside.util.CommonUtil
import com.novashen.riverside.util.Constant
import com.novashen.util.ColorUtil.getAttrColor

/**
 * Created by sca_tl at 2023/6/2 13:40
 */
class WebViewActivity: BaseVBActivity<WebViewPresenter, WebViewView, ActivityWebViewBinding>(), WebViewView {

    private var url: String? = "https://www.example.com"
    private var agentWeb: AgentWeb? = null

    override fun getViewBinding() = ActivityWebViewBinding.inflate(layoutInflater)

    override fun initPresenter() = WebViewPresenter()

    override fun getIntent(intent: Intent?) {
        super.getIntent(intent)
        intent?.let {
            url = it.getStringExtra(Constant.IntentKey.URL)
        }
    }

    override fun initView(theftProof: Boolean) {
        super.initView(false)
        agentWeb = AgentWeb
                .with(this)
                .setAgentWebParent(mBinding.container, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(getAttrColor(this, R.attr.colorPrimary))
                .setWebChromeClient(object : WebChromeClient() {
                    override fun onReceivedTitle(view: WebView, title: String) {
                        mBinding.toolbar.title = title
                        super.onReceivedTitle(view, title)
                    }
                })
                .createAgentWeb()
                .ready()
                .go(url)
    }

    override fun onOptionsSelected(item: MenuItem?) {
        if (item?.itemId == R.id.menu_open_by_system) {
            CommonUtil.openBrowser(this, url)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (agentWeb?.handleKeyEvent(keyCode, event) == true) {
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agentWeb?.destroy()
    }

    override fun getContext() = this
}