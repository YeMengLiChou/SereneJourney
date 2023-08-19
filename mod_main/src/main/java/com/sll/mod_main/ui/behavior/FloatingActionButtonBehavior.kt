package com.sll.mod_main.ui.behavior

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationSet
import android.widget.RelativeLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.ScrollingView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.isGone
import com.sll.lib_framework.ext.view.isVisible
import com.sll.lib_framework.ext.view.visible
import com.sll.lib_framework.util.LogUtils
import kotlin.math.abs

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/07
 */
class FloatingActionButtonBehavior(
    context: Context,
    attributeSet: AttributeSet
): CoordinatorLayout.Behavior<FloatingActionButton>(context, attributeSet) {
    companion object {
        private val TAG = FloatingActionButtonBehavior::class.simpleName
    }

    // 依赖于 AppbarLayout 的展开
    override fun layoutDependsOn(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    // 根据 AppBarLayout 设置 fab 的显式和隐藏
    override fun onDependentViewChanged(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        if (!(dependency is AppBarLayout)) return false
        if (child.isShown && abs(dependency.y.toInt()) == 0) {
            child.hide()
        } else if (child.isOrWillBeHidden && abs(dependency.y.toInt()) != 0){
            child.show()
        }
        return true
    }

}