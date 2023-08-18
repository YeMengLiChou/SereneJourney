package com.sll.lib_framework.ext.view

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sll.lib_framework.decoration.LDividerItemDecoration
import com.sll.lib_framework.ext.dp
import kotlin.math.abs

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */

private var isLock = true

fun RecyclerView.addOnVerticalScrollListener (
    onScrolledUp: (recyclerView: RecyclerView) -> Unit,
    onScrolledDown: (recyclerView: RecyclerView) -> Unit,
    onScrolledToTop: (recyclerView: RecyclerView) -> Unit,
    onScrolledToBottom: (recyclerView: RecyclerView) -> Unit,
    once: Boolean = true // 上下滑动仅触发一次
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!recyclerView.canScrollVertically(-1)) { // 不能继续向上滚动 (手指下拉)
                // 顶部
                onScrolledUp.invoke(recyclerView)
            } else if (!recyclerView.canScrollVertically(1)) {
                // 底部
                onScrolledDown.invoke(recyclerView)
            } else if (dy < 0 && abs(dy) > ViewConfiguration.get(context).scaledTouchSlop) {
                // 往上滑
                if (once && !isLock) {
                    isLock = true
                    onScrolledToTop.invoke(recyclerView)
                } else if (!once) {
                    onScrolledToTop.invoke(recyclerView)
                }
            } else if (dy > 0 && abs(dy) > ViewConfiguration.get(context).scaledTouchSlop) {
                //往下滑
                if (once && isLock) {
                    isLock = false
                    onScrolledToBottom.invoke(recyclerView)
                } else if (!once) {
                    onScrolledToBottom.invoke(recyclerView)
                }
            }
        }
    })
}

/**
 * 设置分割线
 * @param color 分割线的颜色，默认是#DEDEDE
 * @param size 分割线的大小，默认是1px
 * @param includeLast 最后一条是否绘制
 *
 */
fun RecyclerView.divider(
    color: Int = Color.parseColor("#DEDEDE"),
    size: Int = 1,
    includeLast: Boolean = true
): RecyclerView {
    val decoration = LDividerItemDecoration(context, orientation)
    decoration.setDrawable(GradientDrawable().apply {
        setColor(color)
        shape = GradientDrawable.RECTANGLE
        setSize(size, size)
    })
    decoration.isDrawLastPositionDivider(includeLast)
    if (itemDecorationCount > 0) {
        removeItemDecorationAt(0)
    }
    addItemDecoration(decoration)
    return this
}

/**
 * 设置网格分割线间隔
 * @param spanCount 网格的列数
 * @param spacingDp 分割线的大小
 * @param includeEdge 是否包含边缘
 *
 */
fun RecyclerView.dividerGridSpace(
    spanCount: Int,
    spacingDp: Float,
    includeEdge: Boolean
): RecyclerView {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column
            val targetSpacing = spacingDp.dp.toInt()
            if (includeEdge) {
                outRect.left =
                    targetSpacing - column * targetSpacing / spanCount // targetSpacing - column * ((1f / spanCount) * targetSpacing)
                outRect.right =
                    (column + 1) * targetSpacing / spanCount // (column + 1) * ((1f / spanCount) * targetSpacing)
                if (position < spanCount) { // top edge
                    outRect.top = targetSpacing
                }
                outRect.bottom = targetSpacing // item bottom
            } else {
                outRect.left =
                    column * targetSpacing / spanCount // column * ((1f / spanCount) * targetSpacing)
                outRect.right =
                    targetSpacing - (column + 1) * targetSpacing / spanCount // targetSpacing - (column + 1) * ((1f /    spanCount) * targetSpacing)
                if (position >= spanCount) {
                    outRect.top = targetSpacing // item top
                }
            }
        }
    })
    return this
}

fun RecyclerView.vertical(spanCount: Int = 0, isStaggered: Boolean = false): RecyclerView {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    if (spanCount != 0) {
        layoutManager = GridLayoutManager(context, spanCount)
    }
    if (isStaggered) {
        layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }
    return this
}

fun RecyclerView.itemAnimator(animator: RecyclerView.ItemAnimator): RecyclerView {
    itemAnimator = animator
    return this
}

fun RecyclerView.noItemAnim(){
    itemAnimator = null
}

fun RecyclerView.horizontal(spanCount: Int = 0, isStaggered: Boolean = false): RecyclerView {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    if (spanCount != 0) {
        layoutManager = GridLayoutManager(context, spanCount, GridLayoutManager.HORIZONTAL, false)
    }
    if (isStaggered) {
        layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL)
    }
    return this
}

inline val RecyclerView.orientation
    get() = if (layoutManager == null) -1 else layoutManager.run {
        when (this) {
            is GridLayoutManager -> orientation
            is LinearLayoutManager -> orientation
            is StaggeredGridLayoutManager -> orientation
            else -> -1
        }
    }

/**
 * 滑动距离
 * */
class ScrollDistance {
    var x = 0
    var y = 0

    override fun toString(): String {
        return "ScrollDistance: ($x $y)"
    }
}

/**
 * 滑动距离，在 RecyclerView 滑动前调用该方法
 *
 * 仅适用于 item 没有任何更改
 * */
fun RecyclerView.scrollDistance(distance: ScrollDistance) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            distance.y -= dy
        }
    })
}

/**
 * 检查是否已经到达顶部
 * */
fun RecyclerView.checkReachTop(): Boolean {
    if (orientation == RecyclerView.VERTICAL) {
        return !this.canScrollVertically(-1)
    } else {
        return !this.canScrollHorizontally(-1)
    }
}

/**
 * 检查是否已经到达底部
 * */
fun RecyclerView.checkReachBottom(): Boolean {
    if (orientation == RecyclerView.VERTICAL) {
        return !this.canScrollVertically(1)
    } else {
        return !this.canScrollHorizontally(1)
    }
}