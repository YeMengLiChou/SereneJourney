package com.sll.mod_login.service

import android.content.Context
import android.content.Intent
import com.sll.lib_common.constant.PATH_LOGIN_ACTIVITY_LOGIN
import com.sll.lib_common.entity.dto.LoginInfo
import com.sll.lib_common.service.ILoginService
import com.sll.lib_framework.helper.AppHelper
import com.sll.mod_login.manager.LoginInfoManager
import com.therouter.TheRouter
import com.therouter.inject.ServiceProvider
import kotlinx.coroutines.flow.StateFlow

/**
 * 暴露给外部组件调用的接口
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/09
 */
object LoginService: ILoginService {

    @JvmStatic
    @ServiceProvider(returnType = ILoginService::class)
    fun getLoginService() = LoginService

    override fun getLoginInfoFlow(): StateFlow<LoginInfo?> {
        return LoginInfoManager.loginInfo
    }

    // 是否已经登录，无联网检查
    override fun isLogin(): Boolean {
        return LoginInfoManager.loginState
    }

    // 判断本地登录状态是否已经失效
    override fun isExpired(): Boolean {
        return LoginInfoManager.checkExpired()
    }

    // 检查是否登录，联网检查
    override suspend fun checkLogin(): Boolean {
        return LoginInfoManager.checkLogin()
    }

    override fun logout() {
        LoginInfoManager.clearLogInfo()
    }

    override fun setLoginUsername(username: String) {
        LoginInfoManager.setUsernameLocal(username)
    }

    override fun navigate(context: Context): Intent {
        return TheRouter.build(PATH_LOGIN_ACTIVITY_LOGIN).createIntent(context)
    }
}

