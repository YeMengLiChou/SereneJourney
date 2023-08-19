package com.sll.mod_login.manager

import android.util.Log
import com.sll.lib_common.entity.dto.LoginInfo
import com.sll.lib_common.entity.encoder.LoginInfoEncoder
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.manager.KvManager
import com.sll.lib_framework.util.debug
import com.sll.lib_network.manager.ApiManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

/**
 * 管理登录信息
 *
 * 维护一个登陆时间（指手动登录）
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/09
 */
object LoginInfoManager {

    private const val TAG = "LoginInfoManager"

    private const val KEY_LOGIN_INFO = "login_login_info"

    // 内存中维护一份信息
    private var _loginInfo: MutableStateFlow<LoginInfo?> = MutableStateFlow(null)
    val loginInfo = _loginInfo.asStateFlow()

    // 登录状态
    val loginState get() = _loginInfo.value != null

    fun init() {
        _loginInfo.value = getLoginInfoLocal()
        Log.i(TAG, "$TAG init: ${_loginInfo.value}")
    }

    /**
     * 登录信息放入，需要登录成功时放入
     * */
    fun saveLoginInfo(username: String, password: String, isRemember: Boolean) {
        val stamp = System.currentTimeMillis()
        val loginInfo = LoginInfo(username, password, isRemember, stamp)
        _loginInfo.value = loginInfo // 更新内存值
        saveLoginInfoLocal(loginInfo) // 更新本地缓存
    }

    /**
     * 获取登录信息，如果没有返回空
     *
     * @return LoginInfo，没有登录时为null
     * */
    fun getLoginInfo(): LoginInfo? {
        return _loginInfo.value
    }

    /**
     * 清空登录信息，包括本地缓存
     * */
    fun clearLogInfo() {
        _loginInfo.value = null
        clearLoginInfoLocal()
    }


    /**
     * 检查登录是否失效，当前时间与上次登录时间相比较，差距大于等于7天为登录失效
     *
     * @return 失效了返回 true
     * */
    fun checkExpired(): Boolean {
        val cur = System.currentTimeMillis()
        return _loginInfo.value != null && abs(cur - (_loginInfo.value?.timeStamp ?: 0)) >= (7 * 24 * 60 * 60 * 1000)
    }

    /**
     * 检查是否已经登录，联网检查
     *
     * @return 登陆了返回 true
     * */
    suspend fun checkLogin(): Boolean {
        // 本地的登录状态
        val localLoginState = _loginInfo.value != null && !checkExpired()
        if (localLoginState) {
            val localUsername = _loginInfo.value!!.username
            val localPassword = _loginInfo.value!!.password
            val res = ApiManager.api.login(localUsername, localPassword)
            // 请求成功
            if (res.isSuccess()) {
                res.data?.let { // 返回不为空表示登录成功
                    ServiceManager.userService.saveUserInfo(it) // 更新本地信息
                    return true
                } ?: run { // 返回为空表示登录失败
                    ServiceManager.userService.clearUserInfo() // 清空本地信息
                    return false
                }
            } // 请求失败默认使用本地状态
        }
        if (!localLoginState) ServiceManager.userService.clearUserInfo()
        return localLoginState
    }

    /**
     * 使用 [updateUserInfo][com.sll.mod_user.manager.UserInfoManager.updateUserInfo] 更新 username 时，
     * 会顺便把登录时的 username 更改（这接口就逆天）
     * 这里同步修改一下
     * */
    fun setUsernameLocal(username: String) {
        if (_loginInfo.value == null) return
        val updated = _loginInfo.value!!.run {
            LoginInfo(
                username,
                this.password,
                this.remember,
                this.timeStamp
            )
        }
        _loginInfo.value = updated
        saveLoginInfoLocal(updated)
    }

    private fun getLoginInfoLocal(): LoginInfo? {
        return KvManager.encryptedKv.getObject(KEY_LOGIN_INFO)
    }

    private fun saveLoginInfoLocal(loginInfo: LoginInfo) {
        KvManager.encryptedKv.putObject(KEY_LOGIN_INFO, loginInfo, LoginInfoEncoder)
    }

    private fun clearLoginInfoLocal() {
        KvManager.encryptedKv.putObject(KEY_LOGIN_INFO, null, LoginInfoEncoder)
    }
}