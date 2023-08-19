package com.sll.mod_login

import com.sll.mod_login.ui.LoginViewModel

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/11
 */
object CheckParamsUtils {


    /**
     * 检查用户名合法
     * */
     fun checkUsernameValidate(username: String?): Boolean {
        if (username.isNullOrEmpty()) return false
        if (username.length >= LoginViewModel.MAX_USERNAME_LENGTH) return false

        return true
    }

    /**
     * 检查 密码 合法
     * */
    fun checkPasswordValidate(password: String?): Boolean {
        if (password.isNullOrEmpty()) return false
        if (password.length >= LoginViewModel.MAX_PASSWORD_LENGTH) return false

        return true
    }

    /**
     * 检查 用户名和密码 合法
     * */
    fun checkUsernamePwd(username: String?, password: String?): Boolean {
        return checkUsernameValidate(username) && checkPasswordValidate(password)
    }
}