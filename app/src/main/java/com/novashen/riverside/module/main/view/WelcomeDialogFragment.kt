package com.novashen.riverside.module.main.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.novashen.riverside.R
import com.novashen.riverside.databinding.DialogWelcomeBinding
import com.novashen.riverside.util.SharePrefUtil

/**
 * 欢迎弹窗
 * 首次打开应用时显示
 */
class WelcomeDialogFragment : DialogFragment() {

    private var _binding: DialogWelcomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): WelcomeDialogFragment {
            return WelcomeDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置圆角对话框样式
        dialog?.window?.setBackgroundDrawableResource(R.drawable.csu_shape_activity_round_corner)

        binding.btnStart.setOnClickListener {
            // 标记已显示过欢迎页
            SharePrefUtil.setWelcomeShown(requireContext(), true)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
