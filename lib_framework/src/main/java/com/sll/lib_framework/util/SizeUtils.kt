package com.sll.lib_framework.util

import com.sll.lib_framework.helper.AppHelper

/**
 * View 扩展类
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */
object SizeUtils {

    private val displayMetrics get() =  AppHelper.application.resources.displayMetrics

    fun dp2px(dpValue: Float): Float {
        return dpValue * displayMetrics.density
    }

    fun px2dp(pxValue: Float): Float {
        return pxValue / displayMetrics.density
    }

    fun sp2px(spValue: Float): Float {
        return spValue * displayMetrics.scaledDensity
    }

    fun px2sp(pxValue: Float): Float {
        return pxValue / displayMetrics.scaledDensity
    }
}


