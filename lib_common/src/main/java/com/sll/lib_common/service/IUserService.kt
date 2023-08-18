package com.sll.lib_common.service

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sll.lib_common.entity.dto.User
import kotlinx.coroutines.flow.StateFlow

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
interface IUserService {

    fun saveUserInfo(user: User)

    fun getUserInfo(): User?

    fun getUserInfoFlow(): StateFlow<User?>

    fun clearUserInfo()

    fun navigate(context: Context): Intent

    fun navigateToModify(): Fragment
}