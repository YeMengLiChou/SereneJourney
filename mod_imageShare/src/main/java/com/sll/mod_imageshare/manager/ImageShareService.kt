package com.sll.mod_imageshare.manager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.sll.lib_common.service.IImageShareService
import com.sll.mod_imageshare.ui.fragment.ImageShareFragment
import com.therouter.inject.ServiceProvider

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
object ImageShareService : IImageShareService {

    @JvmStatic
    @ServiceProvider(returnType = IImageShareService::class)
    fun getService() = this


    override fun navigateDiscoverFragment(containerId: Int): Fragment {
        return ImageShareFragment(containerId, ImageShareFragment.TYPE_DISCOVER)
    }

    override fun navigateFocusFragment(containerId: Int): Fragment {
        return ImageShareFragment(containerId, ImageShareFragment.TYPE_FOCUS)
    }

    override fun navigateLikeFragment(containerId: Int): Fragment {
        return ImageShareFragment(containerId, ImageShareFragment.TYPE_LIKE)
    }

    override fun navigateCollectFragment(containerId: Int): Fragment {
        return ImageShareFragment(containerId, ImageShareFragment.TYPE_COLLECT)
    }
}