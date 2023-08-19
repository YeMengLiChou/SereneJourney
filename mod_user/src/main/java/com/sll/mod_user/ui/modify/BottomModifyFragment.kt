package com.sll.mod_user.ui.modify

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sll.lib_framework.base.interfaces.IViewBinding
import com.sll.lib_framework.ext.lazyNone
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.util.ImeUtils
import com.sll.mod_user.R
import com.sll.mod_user.databinding.UserFragmentModifyBottomBinding

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/15
 */
class BottomModifyFragment :
    BottomSheetDialogFragment(),
    IViewBinding<UserFragmentModifyBottomBinding>
{
    companion object {
        private const val TAG = "BottomModifyFragment"
    }

    private var contentView: View? = null

    private var title: String? = null

    private var action: OnDialogAction? = null

    private var topBarVisible = true

    private val binding by lazyNone { initViewBinding() }

    // 用于交互
    interface OnDialogAction {
        fun onStart()

        fun onCancel()

        fun onConfirm()
    }


    // dialog消失，回调
    override fun onDismiss(dialog: DialogInterface) {
        action?.onCancel() //
        getDialog()?.hide()
        binding.userFrameLayoutContainer.removeView(contentView) // view需要移除，下次重新加入
        super.onDismiss(dialog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.user_TransparentBottomSheetStyle)

    }

    @SuppressLint("ResourceType", "PrivateResource")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contentView?.let { binding.userFrameLayoutContainer.addView(it) }
        binding.userTvTitle.text = title
        // 取消
        binding.userTvCancel.click {
            dismiss()
        }
        // 确定
        binding.userTvConfirm.click {
            action?.onConfirm()
            dismiss()
        }
        // 顶部栏可见
        if (!topBarVisible) binding.userGroupTopbar.visibility = View.GONE
        
        // 软键盘高度适配
        ImeUtils.listener()
            .onChange { _, _, offset ->
                dialog?.window?.decorView?.findViewById<FrameLayout>(com.google.android.material.R.id.container)!!.y -= offset
            }
            .build(dialog?.window!!)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        action?.onStart()
    }

    override fun initViewBinding(container: ViewGroup?): UserFragmentModifyBottomBinding {
        return UserFragmentModifyBottomBinding.inflate(layoutInflater)
    }

    fun setConfirmEnable(enable: Boolean) {
        binding.userTvConfirm.isEnabled = enable
    }

    fun setTitle(title: String): BottomModifyFragment {
        this.title = title
        return this
    }

    fun setContentView(view: View): BottomModifyFragment {
        this.contentView = view
        return this
    }

    fun setAction(action: OnDialogAction): BottomModifyFragment {
        this.action = action
        return this
    }

    fun setTopBarVisibility(visible: Boolean): BottomModifyFragment {
        this.topBarVisible = visible
        return this
    }


}