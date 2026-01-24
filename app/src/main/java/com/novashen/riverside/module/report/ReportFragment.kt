package com.novashen.riverside.module.report

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.button.MaterialButton
import com.novashen.riverside.R
import com.novashen.riverside.annotation.ToastType
import com.novashen.riverside.base.BaseDialogFragment
import com.novashen.riverside.entity.ReportBean
import com.novashen.riverside.util.Constant

/**
 * Created by sca_tl on 2022/12/16 14:55
 */
class ReportFragment: BaseDialogFragment<ReportPresenter>(), ReportView {

    private lateinit var radioGroup: RadioGroup
    private lateinit var edittext: AppCompatEditText
    private lateinit var submit: MaterialButton

    private var mType: String = ""
    private var mId: Int = Int.MAX_VALUE

    companion object {
        fun getInstance(bundle: Bundle?) = ReportFragment().apply {
                arguments = bundle
        }
    }

    override fun getBundle(bundle: Bundle?) {
        bundle?.let {
            mType = it.getString(Constant.IntentKey.TYPE, "")
            mId = it.getInt(Constant.IntentKey.ID)
        }
    }

    override fun setLayoutResourceId() = R.layout.fragment_report

    override fun findView() {
        radioGroup = view.findViewById(R.id.radio_group)
        edittext = view.findViewById(R.id.edittext)
        submit = view.findViewById(R.id.submit)
    }

    override fun initView() {
        submit.setOnClickListener(this)
    }

    override fun initPresenter() = ReportPresenter()

    override fun getContext() = mActivity

    override fun onClickListener(view: View?) {
        if (view == submit) {
            val radioButton: RadioButton = radioGroup.findViewById(radioGroup.checkedRadioButtonId)
            val s = radioButton.text.toString()
            val msg = "[" + s + "]" + edittext.text.toString()
            presenter.report(mType, msg ,mId)
        }
    }

    override fun onReportSuccess(reportBean: ReportBean) {
        showToast(reportBean.errcode, ToastType.TYPE_ERROR)
        dismiss()
    }

    override fun onReportError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }
}