package com.sll.lib_common.service

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
interface IImageShareService {

    fun navigateDiscoverFragment(fragmentManager: FragmentManager, @IdRes containerId: Int)

    fun navigateDiscoverFragment(): Fragment

}