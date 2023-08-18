package com.sll.lib_framework.ext.view

import android.graphics.Matrix
import android.widget.ImageView

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/07
 */

fun ImageView.imageTranslate(deltaX: Float, deltaY: Float): ImageView {
    this.imageMatrix.setTranslate(deltaX, deltaY)
    return this
}