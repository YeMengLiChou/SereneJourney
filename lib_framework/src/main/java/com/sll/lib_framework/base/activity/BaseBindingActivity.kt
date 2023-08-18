package com.sll.lib_framework.base.activity

import android.os.Bundle
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
abstract class BaseBindingActivity <VB : ViewBinding> : BaseActivity(), IViewBinding<VB> {

    protected open val binding: VB by lazyNone { initViewBinding() }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onDefCreate(savedInstanceState)
        setContentView(binding.root)
    }
    /**
     * 替代 [onCreate] 方法
     * */
    abstract fun onDefCreate(savedInstanceState: Bundle?)
}