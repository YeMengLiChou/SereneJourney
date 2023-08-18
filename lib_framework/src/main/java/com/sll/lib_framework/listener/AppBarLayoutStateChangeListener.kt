package com.sll.lib_framework.listener

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

/**
 * 监听器，建议维护实例，
 *
 * 该监听器集成到 [LAppBarLayout][com.sll.lib_framework.widget.LAppBarLayout]
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/05
 */
abstract class AppBarLayoutStateChangeListener : AppBarLayout.OnOffsetChangedListener {
    companion object {
        private val TAG = AppBarLayoutStateChangeListener::class.simpleName
    }
    // 状态枚举
    enum class State {
        // 展开
        EXPANDED,
        // 收缩
        COLLAPSED,
        // 中间
        IDLE
    }
    var currentState: State = State.EXPANDED
        private set

    var currentOffset: Int = 0
        private set



    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout?.apply {
            currentOffset = verticalOffset
            if (verticalOffset == 0) {
                if (currentState != State.EXPANDED) {
                    onStateChange(appBarLayout, State.EXPANDED, verticalOffset)
                    currentState = State.EXPANDED
                }
            } else {
                if (abs(verticalOffset) >= this.totalScrollRange) {
                    if (currentState != State.COLLAPSED) {
                        onStateChange(appBarLayout, State.COLLAPSED, verticalOffset)
                        currentState = State.COLLAPSED
                    }
                } else {
                    if (currentState != State.IDLE) {
                        onStateChange(appBarLayout, State.IDLE, verticalOffset)
                        currentState = State.IDLE
                    }
                }
            }
        }
    }

    abstract fun onStateChange(appBarLayout: AppBarLayout, state: State, verticalOffset: Int)
}