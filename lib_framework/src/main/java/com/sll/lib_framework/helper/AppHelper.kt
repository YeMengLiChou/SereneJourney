package com.sll.lib_framework.helper

import android.app.Application
import com.therouter.TheRouter
import com.therouter.app.flowtask.lifecycle.FlowTask
import com.therouter.flow.TheRouterFlowTask

/**
 *
 * App 帮助类
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */

object AppHelper {

    lateinit var application: Application
        private set

    var isDebug = false
        private set

    fun init(application: Application, isDebug: Boolean) {
        this.application = application
        this.isDebug = isDebug
    }
}