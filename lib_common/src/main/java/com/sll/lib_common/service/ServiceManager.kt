package com.sll.lib_common.service

import com.therouter.TheRouter

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/12
 */
object ServiceManager {

    /**
     * login 服务
     * */
    val loginService : ILoginService by lazy {
        TheRouter.get(ILoginService::class.java)!!
    }


    val settingService : ISettingService by lazy {
        TheRouter.get(ISettingService::class.java)!!
    }

    val userService : IUserService by lazy {
        TheRouter.get(IUserService::class.java)!!
    }

    val isService: IImageShareService by lazy {
        TheRouter.get(IImageShareService::class.java)!!
    }

    val detailService: IDetailService by lazy {
        TheRouter.get(IDetailService::class.java)!!
    }

    val newShareService: INewShareService by lazy {
        TheRouter.get(INewShareService::class.java)!!
    }

}
