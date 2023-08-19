package com.sll.mod_login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_network.api.params.UserParam
import com.sll.lib_network.ext.requestResponse
import com.sll.lib_network.manager.ApiManager
import com.sll.lib_network.response.Res
import com.sll.mod_login.manager.LoginInfoManager
import com.sll.mod_login.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/09
 */
class LoginViewModel : ViewModel() {
    companion object {
        private val TAG = LoginViewModel::class.simpleName

        const val POSITION_LOGIN = 0

        const val POSITION_REGISTER = 1

        // 用户名最大长度
        const val MAX_USERNAME_LENGTH = 15

        // 密码最大长度
        const val MAX_PASSWORD_LENGTH = 20

    }

    // ================= Activity 界面状态信息 ================

    // 当前页面的 position
    private val _curItemPosition = MutableStateFlow(0)
    val curItemPosition = _curItemPosition.asStateFlow()

    /**
     *  登录状态，LoginActivity 控制是否退出当前界面
     *  - 登录失败时， User 为 null
     *  - 登录成功时， User 不为 null
     * */
    private val _loginState = MutableStateFlow<Res<User?>?>(null)
    val loginState = _loginState.asStateFlow()


    // 切换到 Login
    fun switchToLoginFragment() {
        _curItemPosition.value = POSITION_LOGIN
    }

    // 切换到 register
    fun switchToRegisterFragment() {
        _curItemPosition.value = POSITION_REGISTER
    }
    // =================== LoginFragment 界面数据 ==================

    // 登录用户名
    private val _loginUsername = MutableStateFlow("")
    val loginUsername = _loginUsername.asStateFlow()

    // 登录密码
    private val _loginPassword = MutableStateFlow("")
    val loginPassword = _loginPassword.asStateFlow()

    // 登录记住密码状态
    private val _loginRemember = MutableStateFlow(false)
    val loginRemember = _loginRemember.asStateFlow()


    fun setLoginUsername(username: String) {
        _loginUsername.value = username
    }

    fun setLoginPassword(password: String) {
        _loginPassword.value = password
    }

    fun setRememberChecked(checked: Boolean) {
        _loginRemember.value = checked
    }

    fun initLoginInfoFromLocal() {
        LoginInfoManager.loginInfo.value.let {
            val remember = it?.remember ?: false
            _loginRemember.value = remember
            if (remember && it != null) {
                _loginUsername.value = it.username
                _loginPassword.value = it.password
            }
        }
    }


    // =================== RegisterFragment 界面数据 ==================

    // UI数据
    var registerUsername = ""

    var registerPassword = ""

    var registerPasswordRepeat = ""

    // 注册状态
    private val _registerState = MutableStateFlow<Res<String?>?>(null)
    val registerState = _registerState.asStateFlow()


    // ==================== 网络请求方法 =========================

    /**
     * 登录
     * */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            requestResponse {
                LoginRepository.login(username, password)
            }.collect { res ->
                _loginState.value = res
                res
                    .onSuccess { user ->
                        user?.let {
                            // 记住密码，用于下次检测登录
                            LoginInfoManager.saveLoginInfo(username, password, _loginRemember.value)

                            ServiceManager.userService.saveUserInfo(it)
                        }
                    }
            }
        }
    }

    /**
     * 注册
     *
     * */
    fun register(username: String, password: String) {
        viewModelScope.launch {
            requestResponse {
                ApiManager.api.register(UserParam(username, password))
            }.collect {
                _registerState.value = it
                it.onSuccess {
                    // 登录成功时，将用户名和密码放在login部分的数据
                    _loginUsername.value = username
                    _loginPassword.value = password
                }
            }
        }
    }
}