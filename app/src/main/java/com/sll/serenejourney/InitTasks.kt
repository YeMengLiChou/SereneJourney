package com.sll.serenejourney

import android.app.Application
import com.sll.lib_framework.helper.AppHelper
import com.sll.lib_framework.manager.AppManager
import com.sll.lib_framework.manager.KvManager
import com.sll.lib_framework.starter.task.Task
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.lib_framework.util.ToastUtils
import com.sll.mod_login.manager.LoginInfoManager
import com.sll.mod_user.manager.UserInfoManager
import io.fastkv.interfaces.FastEncoder

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */


class InitUtilstTask(
    private val application: Application
) : Task() {
    override fun run() {
        ToastUtils.init(application)
        SystemBarUtils.init(application)
    }
}

class InitAppHelperTask (
    private val application: Application,
    private val isDebug: Boolean
) : Task() {
    override fun run() {
        AppHelper.init(application, isDebug)
    }
}


class InitAppManagersTask(
    private val application: Application
) : Task() {
    override fun run() {
        AppManager.init(application)
    }
}

/**
 * 初始化键值对
 * */
class InitKvManagerTask(
    private val application: Application,
    private val encoders: MutableList<FastEncoder<*>>
) : Task() {
    override fun run() {
        KvManager.init(application, encoders)
        LoginInfoManager.init()
        UserInfoManager.init()
    }
}
