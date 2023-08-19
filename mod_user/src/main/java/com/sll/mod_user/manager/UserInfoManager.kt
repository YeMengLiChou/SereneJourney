package com.sll.mod_user.manager

import android.util.Log
import com.sll.lib_common.entity.Sex
import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.entity.encoder.UserEncoder
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.manager.KvManager
import com.sll.lib_network.manager.ApiManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 负责本地的用户数据管理
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/12
 */
object UserInfoManager {
    private const val TAG = "UserInfoManager"

    private var _userInfo: MutableStateFlow<User?> = MutableStateFlow(null)

    val userInfo = _userInfo.asStateFlow()

    private const val KEY_USERINFO = "user_userinfo"


    // application Init 调用
    fun init() {
        _userInfo.value = getUserInfoLocal()
        Log.i(TAG, "init: $_userInfo")
    }

    // 保存用户信息
    fun saveUserInfo(user: User) {
        _userInfo.value = user
        saveUserInfoLocal(user)
    }

    // 获取用户信息
    fun getUserInfo(): User? {
        if (_userInfo.value == null) _userInfo.value = getUserInfoLocal()
        return _userInfo.value
    }

    /**
     * 更新已有的用户信息，本地的数据不能为空
     * */
    fun updateUserInfo(
        username: String? = null,
        sex: Sex? = null,
        avatar: String? = null,
        introduce: String? = null,
        lastUpdateTime: Long = 0,
    ) {
        check(_userInfo.value != null) {
            "The local user info must be not null!"
        }
        if (_userInfo.value == null) return
        val updateUsername = username ?: _userInfo.value!!.username
        val updateSex = sex?.key ?: _userInfo.value!!.sex
        val updateAvatar = avatar ?: _userInfo.value!!.avatar
        val updateIntroduce = introduce ?: _userInfo.value!!.introduce
        val updateLastUpdateTime = if (lastUpdateTime != 0L) lastUpdateTime else _userInfo.value!!.lastUpdateTime
        _userInfo.value = _userInfo.value?.run {
            User(
                appKey,
                updateAvatar,
                createTime,
                id,
                updateIntroduce,
                updateLastUpdateTime,
                password,
                updateSex,
                updateUsername
            )
        }
        // 本地注册的也需要修改
        username?.let { ServiceManager.loginService.setLoginUsername(it) }
        saveUserInfoLocal(_userInfo.value!!)
    }
    // 清除用户信息，包括本地
    fun clearUserInfo() {
        _userInfo.value = null
        clearUserInfoLocal()
    }

    private fun saveUserInfoLocal(user: User) {
        KvManager.encryptedKv.putObject(KEY_USERINFO, user, UserEncoder)
    }

    private fun clearUserInfoLocal() {
        KvManager.encryptedKv.putObject(KEY_USERINFO, null, UserEncoder)
    }

    private fun getUserInfoLocal(): User? {
        return KvManager.encryptedKv.getObject(KEY_USERINFO)
    }

}