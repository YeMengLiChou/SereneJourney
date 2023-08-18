package com.sll.lib_framework.ext.view

import android.view.View
import android.view.ViewGroup

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/05
 */


operator fun ViewGroup.get(index: Int): View? = this.getChildAt(index)