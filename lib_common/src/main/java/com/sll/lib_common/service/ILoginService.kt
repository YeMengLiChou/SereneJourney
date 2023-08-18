package com.sll.lib_common.service

import android.content.Context
import android.content.Intent
import com.sll.lib_common.entity.dto.LoginInfo
import com.sll.lib_common.entity.dto.User
import kotlinx.coroutines.flow.StateFlow

/**
 *
 * mod_login 提供功能，定义接口
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/09
 */
interface ILoginService {

    fun getLoginInfoFlow(): StateFlow<LoginInfo?>

    // 判断登录状态
    fun isLogin(): Boolean

    suspend fun checkLogin(): Boolean

    // 失效
    fun isExpired(): Boolean

    fun setLoginUsername(username: String)

    fun logout()

    fun navigate(context: Context): Intent
}

//inline fun ILoginService.checkLogin (
//    crossinline onFailure: () -> Unit = {},
//    crossinline onLogin: () -> Unit
//) {
//    if (isLogin()) {
//        if (!isExpired()) onLogin.invoke()
//        else onFailure.invoke()
//    } else onFailure.invoke()
//}


