package com.sll.mod_imageshare.manager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.sll.lib_common.service.IImageShareService
import com.sll.mod_imageshare.ui.item.DiscoverFragment
import com.therouter.app.flowtask.lifecycle.FlowTask
import com.therouter.inject.ServiceProvider

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
object ImageShareService: IImageShareService {

    @JvmStatic
    @ServiceProvider(returnType = IImageShareService::class)
    fun getService() = this


    override fun navigateDiscoverFragment(fragmentManager: FragmentManager, containerId: Int) {
        fragmentManager.commit {

            add(containerId, DiscoverFragment())

        }
    }

    override fun navigateDiscoverFragment(): Fragment {
        return DiscoverFragment()
    }

}