package com.sll.lib_framework.helper

import android.widget.Toast
import kotlin.math.abs
import kotlin.system.exitProcess

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
object AppExit {

    private var lastPressedTime = -1L

    fun exit(currentTime: Long) {
        if (abs(currentTime - lastPressedTime) >= 1500) {
            lastPressedTime = currentTime
            Toast.makeText(AppHelper.application, "再返回一次退出", Toast.LENGTH_SHORT).show()
        } else {
            exitProcess(0)
        }
    }

}