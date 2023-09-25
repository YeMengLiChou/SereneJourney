package com.example.mod_newshare.ui

import android.app.Dialog
import android.nfc.Tag
import android.os.Bundle
import android.provider.DocumentsContract.Root
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mod_newshare.R
import com.example.mod_newshare.databinding.NewshareFragmentReturnBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.view.setClipViewCornerTopRadius
import com.sll.lib_framework.util.ToastUtils

class ReturnFragment : BottomSheetDialogFragment() {
    lateinit var binding: NewshareFragmentReturnBinding
    private var action:onDialogAction ?= null
    companion object {
        const val TAG = "ReturnFragment"
    }
    interface onDialogAction{
        fun saveDragt()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NewshareFragmentReturnBinding.inflate(layoutInflater)
        binding.root.apply {
            this.post {
                setClipViewCornerTopRadius(16.dp)
            }
        }
        initListener()
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.newshare_TransparentBottomSheetStyle)
    }

    fun initListener() {
        binding.save.setOnClickListener {
            action?.saveDragt()
        }
        binding.unsave.setOnClickListener {
            activity?.finish()
        }
        binding.cancel.setOnClickListener{
            dismiss()
        }
    }

    fun setAction(action : onDialogAction):ReturnFragment{
        this.action = action
        return this
    }
}