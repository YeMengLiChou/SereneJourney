package com.sll.mod_user.ui

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sll.lib_common.entity.Sex
import com.sll.lib_common.entity.dto.ImageSet
import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.manager.KvManager
import com.sll.lib_network.ext.requestResponse
import com.sll.lib_network.response.Res
import com.sll.mod_user.manager.UserInfoManager
import com.sll.mod_user.reposiotry.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/14
 */
class UserViewModel: ViewModel() {
    companion object {
        private const val TAG = "UserViewModel"
    }

    // ================== user info ===========================
    val userInfo = UserInfoManager.userInfo

    init {
        // 根据本地的数据初始化，UserInfoManager的数据发生改变时，这里也会发生改变
        viewModelScope.launch {
            UserInfoManager.userInfo.collectLatest {
                it?.apply {
                    updateUsername = username
                    updateSex = when (sex) {
                        Sex.MALE.key -> Sex.MALE
                        Sex.FEMALE.key -> Sex.FEMALE
                        else -> Sex.UNKNOWN
                    }
                    updateAvatar = avatar
                    updateIntroduce = introduce
                }
            }
        }
    }


    // =============== update info =============================

    private val _updateState = MutableStateFlow<Res<*>?>(null)
    val updateState = _updateState.asStateFlow()

    // 头像上传状态
    private val _uploadState = MutableStateFlow<Res<ImageSet?>?>(null)
    val uploadState = _uploadState.asStateFlow()

    private var updateUsername = ""

    private var updateAvatar: String? = null

    private var updateIntroduce: String? = null

    private var updateSex = Sex.UNKNOWN

    // --------------- set memory info ---------------------

    fun setUpdateUsername(username: String) {
        updateUsername = username
    }

    fun setUpdateSex(sex: Int) {
        updateSex = when (sex) {
            Sex.MALE.key -> Sex.MALE
            Sex.FEMALE.key -> Sex.FEMALE
            else -> Sex.UNKNOWN
        }
    }

    fun setUpdateIntroduce(introduce: String) {
        updateIntroduce = introduce
    }


    // ------------ update to server --------------------
    // 更新用户名
    fun updateUsername() {
        val username = updateUsername
        viewModelScope.launch {
            requestResponse {
                UserRepository.updateUserInfo(
                    userId = userInfo.value!!.id,
                    username = username
                )
            }.collect {
                _updateState.value = it
                it.onSuccess { UserInfoManager.updateUserInfo(username = username) }
            }
        }
    }

    // 更新头像
    fun updateAvatar() {
        val avatar = updateAvatar
        viewModelScope.launch {
            requestResponse {
                UserRepository.updateUserInfo(
                    userId = userInfo.value!!.id,
                    avatar = avatar
                )
            }.collect {
                _updateState.value = it
                it.onSuccess {
                    UserInfoManager.updateUserInfo(avatar = avatar)
                    ServiceManager.settingService.setUserAvatarLastUpdateTime(System.currentTimeMillis())
                }
            }
        }
    }

    // 更新个人签名
    fun updateIntroduce() {
        val introduce = updateIntroduce
        viewModelScope.launch {
            requestResponse {
                UserRepository.updateUserInfo(
                    userId = userInfo.value!!.id,
                    introduce = introduce
                )
            }.collect {
                _updateState.value = it
                it.onSuccess { UserInfoManager.updateUserInfo(introduce = introduce) }
            }
        }
    }

    // 更新性别
    fun updateSex() {
        val sex = updateSex
        viewModelScope.launch {
            requestResponse {
                UserRepository.updateUserInfo(
                    userId = userInfo.value!!.id,
                    sex = sex
                )
            }.collect {
                _updateState.value = it
                it.onSuccess { UserInfoManager.updateUserInfo(sex = sex) }
            }
        }
    }

    // 上传头像
    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            requestResponse {
                UserRepository.uploadAvatar(uri)
            }.collect { res ->
                _uploadState.value = res
                res.onSuccess { // 上传头像成功后，就可以更新个人的头像信息
                    val avatarUrl = it?.urls?.get(0)
                    if (avatarUrl != null) {
                        updateAvatar = avatarUrl
                        updateAvatar()
                    }
                }
                uri.toFile().delete() // 删除缓存文件
            }
        }
    }
}
