package com.sll.mod_main.ui.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/04
 */
class ViewPagerBehavior(
    val context: Context,
    private val attributeSet: AttributeSet
): CoordinatorLayout.Behavior<ViewPager2>(context, attributeSet) {
    companion object {
        private val TAG = ViewPagerBehavior::class.simpleName
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ViewPager2, dependency: View): Boolean {
        if (dependency is AppBarLayout) return true
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ViewPager2, dependency: View): Boolean {


        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: ViewPager2, dependency: View) {
        super.onDependentViewRemoved(parent, child, dependency)
    }

}