package com.example.mod_newshare

import android.content.Context
import com.sll.lib_common.constant.PATH_NEWSHARE_ACTIVITY_EDIT
import com.sll.lib_common.service.INewShareService
import com.therouter.TheRouter
import com.therouter.inject.ServiceProvider

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/23
 */
object NewShareService: INewShareService {


    @JvmStatic
    @ServiceProvider(returnType = INewShareService::class)
    fun getService() = this


    override fun navigation(context: Context) {
        val intent = TheRouter.build(PATH_NEWSHARE_ACTIVITY_EDIT).createIntent(context)
        context.startActivity(intent)
    }


}