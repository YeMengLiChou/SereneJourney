package com.sll.mod_main.ui.behavior

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginTop
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sll.lib_framework.ext.view.isVisible
import com.sll.mod_main.R

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/07
 */
class UpperFloatingActionButtonBehavior(
    context: Context,
    attributeSet: AttributeSet
): CoordinatorLayout.Behavior<FloatingActionButton>(context, attributeSet) {
    companion object {
        private val TAG = UpperFloatingActionButtonBehavior::class.simpleName
    }

    private var upper = false
    private val animatorInterpolator = OvershootInterpolator()
    private val animatorDuration: Long = 195


    // 依赖于 FloatingActionButton 的出现
    override fun layoutDependsOn(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        return dependency is FloatingActionButton && child != dependency && dependency.id == R.id.fab_back_to_top
    }

    // 根据 下方的 FAB 设置 fab 位置
    override fun onDependentViewChanged(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        if (!(dependency is FloatingActionButton && child != dependency)) return false
        if (dependency.isShown && !upper) {
            ObjectAnimator
                .ofFloat(child, "translationY", 0f, -(dependency.height + dependency.marginTop).toFloat())
                .apply {
                    this.interpolator = animatorInterpolator
                    this.duration = animatorDuration
                }.start()
            upper = true
        }
        if (dependency.isOrWillBeHidden && upper){
            ObjectAnimator
                .ofFloat(child, "translationY", -(dependency.height + dependency.marginTop).toFloat(), 0f)
                .apply {
                    this.interpolator = animatorInterpolator
                    this.duration = animatorDuration
                }.start()
            upper = false
        }
        return true
    }

}