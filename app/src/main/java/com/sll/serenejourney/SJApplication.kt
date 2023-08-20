package com.sll.serenejourney

import android.app.Application
import com.sll.lib_common.entity.encoder.LoginInfoEncoder
import com.sll.lib_common.entity.encoder.UserEncoder
import com.sll.lib_framework.starter.dispatcher.TaskDispatcher
import com.sll.lib_framework.util.LogUtils

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */
class SJApplication : Application() {
    companion object {
        private val TAG = SJApplication::class.simpleName
    }

    private val isDebug = true

    override fun onCreate() {
        super.onCreate()

        LogUtils.init(this, "SereneJourney", isDebug)
        TaskDispatcher.init(this)

        TaskDispatcher.createInstance()
            .addTask(InitAppHelperTask(this, isDebug))
            .addTask(InitSystemBarUtilsTask(this))
            .addTask(InitUtilstTask(this))
            .addTask(InitAppManagersTask(this))
            .addTask(InitKvManagerTask(this, mutableListOf(UserEncoder, LoginInfoEncoder)))
            .start()
            .await()

    }

}