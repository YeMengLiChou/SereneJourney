package com.sll.lib_framework.ext.view

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.viewpager2.widget.ViewPager2

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/11
 */

/**
 * 控制滚动的速度（自动滚动）
 * @param targetPosition 目标位置
 * @param duration 动画时长
 * */
fun ViewPager2.setCurrentItem (
    targetPosition: Int,
    duration: Long
) {
    var preValue = 0
    val curPosition = currentItem
    val width = width
    val totalScrollDistance = width * (targetPosition - curPosition)
    if (totalScrollDistance == 0) return
    ValueAnimator
        .ofInt(0, totalScrollDistance)
        .apply {
            this.duration = duration
            this.interpolator = LinearInterpolator()
            addUpdateListener {
                val curValue = it.animatedValue as Int
                val curScrollDistance = curValue - preValue
                fakeDragBy(-curScrollDistance.toFloat())
                preValue = curValue
            }
            addListener (
                onStart = { beginFakeDrag() },
                onEnd = { endFakeDrag() }
            )
        }.start()
}

fun <T> ViewPager2.setInfScrollable(items: MutableList<T>, firstItem: T, lastItem: T) {
    items.add(0, firstItem)
    items.add(lastItem)
    var currentPosition = 1
    setCurrentItem(1, false)
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            currentPosition = position
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                if (currentPosition == 0) {
                    setCurrentItem(items.size - 2, false)
                } else if (currentPosition == items.size - 1) {
                    setCurrentItem(1, false)
                }
            }

        }
    })
}