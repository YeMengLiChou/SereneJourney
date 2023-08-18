package com.sll.lib_framework.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.sll.lib_framework.base.interfaces.IViewBinding
import com.sll.lib_framework.ext.lazyNone

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */
abstract class BaseBindingFragment <VB : ViewBinding> : BaseFragment(), IViewBinding<VB> {
    protected open val binding : VB by lazyNone {
        initViewBinding(mContainer)
    }
    private var mContainer: ViewGroup? = null

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mContainer = container
        onDefCreateView()
        return binding.root
    }

    abstract fun onDefCreateView()
}