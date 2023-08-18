package com.sll.lib_framework.ext.res

import android.animation.ValueAnimator

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/11
 */
/**
 * Float 类型的 [addUpdateIntListener]，带有偏移量
 *
 * */
inline fun ValueAnimator.addUpdateFloatListener(crossinline update: (value: Float, fraction: Float, offset: Float) -> Unit) {
    var pre = 0f
    addUpdateListener {
        (it.animatedValue as Float).let { value ->
            update(value, it.animatedFraction, value - pre)
            pre = value
        }
    }
}

inline fun ValueAnimator.addUpdateIntListener(crossinline update: (value: Int, fraction: Float, offset: Int) -> Unit) {
    var pre = 0
    addUpdateListener {
        (it.animatedValue as Int).let { value ->
            update(value, it.animatedFraction, value - pre)
            pre = value
        }
    }
}