package com.sll.lib_common.service

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentManager

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
interface IImageShareService {

    fun navigateDiscoverFragment(@IdRes containerId: Int): Fragment

    fun navigateFocusFragment(@IdRes containerId: Int): Fragment

    fun navigateLikeFragment(@IdRes containerId: Int): Fragment

    fun navigateCollectFragment(@IdRes containerId: Int): Fragment
}