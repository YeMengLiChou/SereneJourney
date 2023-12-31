package com.sll.lib_framework.ext.res

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sll.lib_framework.helper.AppHelper
import com.sll.lib_framework.manager.AppManager

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */
private val res: Resources
    get() = appContext.resources

private val appContext: Context
    get() = AppHelper.application

fun Context.color(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Fragment.color(@ColorRes id: Int) = ContextCompat.getColor(appContext, id)

fun Context.color(color: String) = Color.parseColor(color)

fun Fragment.color(color: String) = Color.parseColor(color)

fun Context.string(@StringRes id: Int) = resources.getString(id)

fun Fragment.string(@StringRes id: Int) = resources.getString(id)

fun Context.stringArray(@ArrayRes id: Int): Array<String> = resources.getStringArray(id)

fun Fragment.stringArray(@ArrayRes id: Int): Array<String> = resources.getStringArray(id)

fun Context.drawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun Fragment.drawable(@DrawableRes id: Int) = ContextCompat.getDrawable(appContext, id)

fun Context.dimenPx(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

fun Fragment.dimenPx(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

fun Context.integer(@IntegerRes id: Int) = resources.getInteger(id)

fun Fragment.integer(@IntegerRes id: Int) = resources.getInteger(id)

inline val Context.layoutInflater: android.view.LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater


val screenWidth: Int get() = AppManager.screenWidthPx

val screenHeight: Int get() = AppManager.screenHeightPx


fun inflateLayout(
    @LayoutRes layoutId: Int,
    parent: ViewGroup? = null,
    attachToParent: Boolean = false
): View {
    return appContext.layoutInflater.inflate(layoutId, parent, attachToParent)
}