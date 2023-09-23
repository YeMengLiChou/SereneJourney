package com.sll.mod_detail.ui.fragment

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.sll.lib_framework.base.interfaces.IViewBinding
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.launchOnStarted
import com.sll.lib_framework.ext.lazyNone
import com.sll.lib_framework.ext.res.color
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.ext.view.setClipViewCornerTopRadius
import com.sll.lib_framework.ext.view.textFlow
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.util.ImeUtils
import com.sll.mod_detail.R
import com.sll.mod_detail.databinding.DetailLayoutBottomCommentBinding

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/19
 */
class CommentBottomFragment :
    BottomSheetDialogFragment(),
    IViewBinding<DetailLayoutBottomCommentBinding> {
    companion object {
        private const val TAG = "CommentBottomFragment"

        const val LENGTH_MAX_LIMIT = 50
    }

    interface OnCommentActionListener {
        fun onStart(root: View)

        fun onDismiss(root: View)

        fun onConfirm()
    }


    private val binding by lazyNone { initViewBinding() }

    private var mTitle: CharSequence? = null

    private var mContent: String = ""

    val commentContentFlow get() = binding.etInput.textFlow()

    val commentContent get() = binding.etInput.text.toString()

    private var mActionListener: OnCommentActionListener? = null

    override fun initViewBinding(container: ViewGroup?): DetailLayoutBottomCommentBinding {
        return DetailLayoutBottomCommentBinding.inflate(layoutInflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.detail_TransparentBottomSheetStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 顶部圆角
        binding.root.post {
            binding.root.setClipViewCornerTopRadius(16.dp)
            binding.etInput.setClipViewCornerRadius(8.dp)
        }
        // 设置多行输入
        binding.etInput.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        binding.etInput.isSingleLine = false
        binding.btSend.throttleClick { mActionListener?.onConfirm() }
        // 软键盘高度适配
        ImeUtils.listener()
            .onChange { _, _, offset ->
                dialog?.window?.decorView?.findViewById<FrameLayout>(com.google.android.material.R.id.container)!!.y -= offset
            }
            .build(dialog?.window!!)
        launchOnStarted {
            commentContentFlow.collect {
                val size = it.length
                when {
                    size == 0 -> {
                        setButtonEnable(false, binding.btSend)
                    }

                    size > LENGTH_MAX_LIMIT -> {
                        setButtonEnable(false, binding.btSend)
                        binding.layoutInput.error = "超出长度限制"
                    }

                    else -> {
                        if (!binding.btSend.isEnabled) {
                            setButtonEnable(true, binding.btSend)
                            binding.layoutInput.error = null
                        }
                    }
                }
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.tvTitle.text = mTitle
        // 默认不能发送
        binding.btSend.isEnabled = false
        binding.etInput.setText(mContent)
        mActionListener?.onStart(binding.root)
    }

    override fun onDismiss(dialog: DialogInterface) {
        mActionListener?.onDismiss(binding.root)
        binding.tvTitle.text = ""
        binding.etInput.setText("")
        super.onDismiss(dialog)
    }


    fun setOnActionListener(listener: OnCommentActionListener): CommentBottomFragment {
        this.mActionListener = listener
        return this
    }

    fun setTitle(title: CharSequence): CommentBottomFragment {
        mTitle = title
        return this
    }

    fun setEditContent(content: String): CommentBottomFragment {
        mContent = content
        return this
    }

    private fun setButtonEnable(enable: Boolean, bt: MaterialButton) {
        bt.isEnabled = enable
        if (enable) {
            bt.backgroundTintList = ColorStateList.valueOf(color(R.color.detail_blue_cornflower))

        } else {
            bt.backgroundTintList = ColorStateList.valueOf(color(R.color.detail_blue_light))

        }
    }



}