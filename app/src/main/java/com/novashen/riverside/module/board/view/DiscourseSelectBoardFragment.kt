package com.novashen.riverside.module.board.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ContextThemeWrapper
import android.view.HapticFeedbackConstants
import android.view.View
import com.google.android.material.chip.Chip
import com.google.android.material.shape.ShapeAppearanceModel
import com.novashen.riverside.R
import com.novashen.riverside.annotation.ToastType
import com.novashen.riverside.api.discourse.entity.CategoriesResponse
import com.novashen.riverside.api.discourse.entity.CategoryDetailResponse
import com.novashen.riverside.base.BaseEvent
import com.novashen.riverside.base.BaseVBBottomFragment
import com.novashen.riverside.databinding.FragmentDiscourseSelectBoardBinding
import com.novashen.riverside.entity.SelectBoardResultEvent
import com.novashen.riverside.module.board.presenter.DiscourseSelectBoardPresenter
import com.novashen.riverside.util.Constant
import com.novashen.riverside.util.showToast
import com.novashen.util.ScreenUtil
import com.novashen.widget.bottomsheet.ViewPagerBottomSheetBehavior
import org.greenrobot.eventbus.EventBus

/**
 * Discourse 板块选择 Fragment
 * 用于选择 Discourse 论坛的板块分类
 */
class DiscourseSelectBoardFragment : BaseVBBottomFragment<DiscourseSelectBoardPresenter, DiscourseSelectBoardView, FragmentDiscourseSelectBoardBinding>(), DiscourseSelectBoardView {

    private var mNeedConfirm = false
    private var mSelectBoardResultEvent = SelectBoardResultEvent()
    private var currentParentCategory: CategoriesResponse.Category? = null

    companion object {
        fun getInstance(bundle: Bundle?) = DiscourseSelectBoardFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        mNeedConfirm = bundle?.getBoolean(Constant.IntentKey.NEED_CONFIRM, false) ?: false
    }

    override fun getViewBinding() = FragmentDiscourseSelectBoardBinding.inflate(layoutInflater)

    override fun initPresenter() = DiscourseSelectBoardPresenter()

    override fun initView() {
        Handler(Looper.getMainLooper()).post {
            mBehavior.state = ViewPagerBottomSheetBehavior.STATE_EXPANDED
        }
        mBinding.confirmBtn.setOnClickListener(this)
        mBinding.statusView.loading(mBinding.scrollView)
        mPresenter?.getParentCategories()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        if (v == mBinding.confirmBtn) {
            dismiss()
        }
    }

    override fun onGetParentCategoriesSuccess(categories: List<CategoriesResponse.Category>) {
        mBinding.statusView.success()
        mBinding.parentCategoriesGroup.removeAllViews()

        categories.forEach { category ->
            val chip = getChip(category.name)
            chip.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                currentParentCategory = category

                // 清空子板块显示
                mBinding.subcategoriesGroup.removeAllViews()
                mBinding.subcategoryTitle.visibility = View.GONE
                mBinding.subcategoriesGroup.visibility = View.GONE
                mBinding.confirmBtn.visibility = View.GONE

                // 如果有子板块，加载子板块
                if (category.subcategoryIds != null && category.subcategoryIds.isNotEmpty()) {
                    mBinding.subcategoryTitle.visibility = View.VISIBLE
                    mBinding.subcategoriesGroup.visibility = View.VISIBLE
                    mPresenter?.getSubcategories(category.subcategoryIds)
                } else {
                    // 没有子板块，直接选择该板块
                    mSelectBoardResultEvent.childBoardId = category.id
                    mSelectBoardResultEvent.childBoardName = category.name

                    if (mNeedConfirm) {
                        mBinding.confirmBtn.apply {
                            visibility = View.VISIBLE
                            text = "确认选择"
                        }
                    } else {
                        dismiss()
                    }
                }
            }
            mBinding.parentCategoriesGroup.addView(chip)
        }
    }

    override fun onGetParentCategoriesError(msg: String?) {
        mBinding.statusView.error()
        showToast("加载板块失败:$msg", ToastType.TYPE_ERROR)
    }

    override fun onGetSubcategoriesSuccess(subcategories: List<CategoryDetailResponse.Category>) {
        mBinding.subcategoriesGroup.removeAllViews()

        if (subcategories.isEmpty()) {
            mBinding.subcategoryTitle.visibility = View.GONE
            mBinding.subcategoriesGroup.visibility = View.GONE
            return
        }

        mBinding.subcategoryTitle.visibility = View.VISIBLE
        mBinding.subcategoriesGroup.visibility = View.VISIBLE

        subcategories.forEach { subcategory ->
            val chip = getChip(subcategory.name)
            chip.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)

                // 选择子板块
                mSelectBoardResultEvent.childBoardId = subcategory.id
                mSelectBoardResultEvent.childBoardName = subcategory.name

                if (mNeedConfirm) {
                    mBinding.confirmBtn.apply {
                        visibility = View.VISIBLE
                        text = "确认选择"
                    }
                } else {
                    dismiss()
                }
            }
            mBinding.subcategoriesGroup.addView(chip)
        }
    }

    override fun setMaxHeightMultiplier() = 0.9

    override fun dismiss() {
        EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.BOARD_SELECTED, mSelectBoardResultEvent))
        super.dismiss()
    }

    private fun getChip(txt: String) = Chip(ContextThemeWrapper(context, R.style.Widget_Material3_Chip_Filter)).apply {
        text = txt
        isCheckable = true
        chipStrokeWidth = 0f
        chipIcon = null
        checkedIcon = null
        setChipBackgroundColorResource(R.color.select_board_chip_bg_color)
        shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
            setAllCornerSizes(ScreenUtil.dip2pxF(requireContext(), 20f))
        }.build()
        chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#00000000"))
    }
}
