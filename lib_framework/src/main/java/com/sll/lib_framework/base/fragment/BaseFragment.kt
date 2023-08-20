package com.sll.lib_framework.base.fragment

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.sll.lib_framework.base.activity.BaseActivity
import com.sll.lib_framework.ext.lazyNone
import com.sll.lib_framework.util.debug

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */
abstract class BaseFragment: Fragment() {

    /**
     * 检查是否已经与 activity attached
     * */
    fun checkAttachedActivity(): Boolean {
        return activity != null
    }


}