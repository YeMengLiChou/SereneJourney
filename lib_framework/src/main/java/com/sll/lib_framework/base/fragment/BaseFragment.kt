package com.sll.lib_framework.base.fragment

import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

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