package com.ssl.mod_detail.manager

import com.sll.lib_common.constant.PATH_DETAIL_ACTIVITY_DETAIL
import com.sll.lib_common.service.IDetailService
import com.sll.lib_common.service.IImageShareService
import com.therouter.TheRouter
import com.therouter.inject.ServiceProvider

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/14
 */
object DetailService: IDetailService {

    @JvmStatic
    @ServiceProvider(returnType = IDetailService::class)
    fun getService() = this


    override fun navigate() {
        TheRouter.build(PATH_DETAIL_ACTIVITY_DETAIL).navigation()
    }
}