package com.sll.lib_common.interfaces

/**
 * 用于统一 Fragment 对外的滑动接口，如果实现了 RecyclerView 等滑动控件，通过该接口可以从外部控制内部的滑动
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/08
 */

interface FragmentScrollable {

    /**
     * 实现该方法用于滑动到顶部
     */
    fun scrollToTop()

    /**
     * 实现该方法用于控制滚动的位置
     * */
    fun scrollTo(x: Int, y: Int)

    /**
     * 实现该方法用于控制滚动的偏移量
     * */
    fun scrollBy(dx: Int, dy: Int)

    /**
     * 实现该方法用于检查是否已经到顶
     * */
    fun checkReachTop(): Boolean
}