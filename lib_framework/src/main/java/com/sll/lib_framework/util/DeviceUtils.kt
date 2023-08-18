package com.sll.lib_framework.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.opengl.Visibility
import android.os.Build
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/11
 */
@SuppressLint("ObsoleteSdkInt")
object DeviceUtils {

    private const val NOT_INITIALIZED = -1

    // 是否已经初始化
    private var initialized = false

    private lateinit var application: Application

    // =============== 设备信息 ===================




    fun init(application: Application) {
        this.application = application
        this.initialized = true
    }

    // 检查初始化
    private fun checkInitialized() {
        check(initialized) {
            "You need initialize DeviceUtils first!"
        }
    }

    /**
     * 判断当前是否为 **夜间模式**
     * @param context
     * */
    fun checkNightMode(context: Context): Boolean {
        return context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) != 0
    }



}