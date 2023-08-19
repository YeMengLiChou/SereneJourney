package com.sll.mod_user.service

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sll.lib_common.constant.PATH_USER_ACTIVITY_USER
import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.service.IUserService
import com.sll.lib_framework.util.debug
import com.sll.mod_user.manager.UserInfoManager
import com.therouter.TheRouter
import com.therouter.inject.ServiceProvider
import kotlinx.coroutines.flow.StateFlow

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
object UserService : IUserService {

    @JvmStatic
    @ServiceProvider(returnType = IUserService::class)
    fun getService() = this

    override fun saveUserInfo(user: User) {
        debug("saveUserInfo", user)
        UserInfoManager.saveUserInfo(user)
    }

    override fun getUserInfo(): User? = UserInfoManager.getUserInfo()

    override fun getUserInfoFlow(): StateFlow<User?> {
        return UserInfoManager.userInfo
    }


    override fun clearUserInfo() {
        UserInfoManager.clearUserInfo()
    }

    override fun navigate(context: Context): Intent {
        return TheRouter.build(PATH_USER_ACTIVITY_USER).createIntent(context)
    }

    override fun navigateToModify(): Fragment {
//        return TheRouter.build(PATH_USER_ACTIVITY_USER).createFragment<>()
        return Fragment()
    }


}