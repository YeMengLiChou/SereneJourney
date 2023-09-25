package com.sll.lib_framework.util

import android.animation.ObjectAnimator
import android.app.Application
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.sll.lib_framework.R
import com.sll.lib_framework.databinding.FrameworkLayoutToastBinding
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.res.tint
import com.therouter.app.flowtask.lifecycle.FlowTask

/**
 * Toast 自定义工具类，因为 [Toast.setView] 被废弃，导致自定义Toast无法在后台弹出,尽量在应用内部使用
 *
 * - 通过 [Location] 可以设置 Toast 的位置
 * - 三种带图标的方法 [success]，[warn] 和 [error]
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */
object ToastUtils {
    /**
     * 中间位置
     * */
    val CENTER = Location(Gravity.CENTER, 0, 0)

    /**
     * 上方居中位置
     * */
    val TOP = Location(Gravity.TOP or Gravity.CENTER, 0, 32.dp)

    /**
     * 下方居中位置
     * */
    val BOTTOM = Location(Gravity.BOTTOM or Gravity.CENTER, 0, 32.dp)

    /**
     * 左方居中位置
     * */
    val LEFT = Location(Gravity.LEFT or Gravity.CENTER, 32.dp, 0)

    /**
     * 右方居中位置
     * */
    val RIGHT = Location(Gravity.RIGHT or Gravity.CENTER, 32.dp, 0)

    // toast 实例
    private var toast: Toast? = null

    private lateinit var mContext: Application

    private val mToastHandler = Looper.myLooper()
        ?.let { Handler(it) }
        ?: Handler(Looper.getMainLooper())

    private val binding by lazy {
        FrameworkLayoutToastBinding.inflate(LayoutInflater.from(mContext), null, false)
    }

    // 文字和图标颜色
    private val textColor = Color.parseColor("#FF585268")

    /**
     * 需要初始化
     * */
    fun init(context: Application) {
        mContext = context
    }

    fun make(
        @StringRes stringId: Int,
        longDuration: Boolean = false,
        @DrawableRes drawableId: Int = 0,
        location: Location = BOTTOM
    ) {
        val msg = mContext.getString(stringId)
        toastImpl(
            msg,
            if (longDuration) Toast.LENGTH_SHORT else Toast.LENGTH_LONG,
            drawableId,
            location
        )
    }

    fun make(
        msg: String,
        longDuration: Boolean = false,
        @DrawableRes drawableId: Int = 0,
        location: Location = BOTTOM
    ) {
        toastImpl(
            msg,
            if (longDuration) Toast.LENGTH_SHORT else Toast.LENGTH_LONG,
            drawableId,
            location
        )
    }

    fun make(
        msg: CharSequence,
        longDuration: Boolean = false,
        @DrawableRes drawableId: Int = 0,
        location: Location = BOTTOM
    ) {
        toastImpl(
            msg,
            if (longDuration) Toast.LENGTH_SHORT else Toast.LENGTH_LONG,
            drawableId,
            location
        )
    }

    fun success(msg: String) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_success,
            BOTTOM
        )
    }

    fun success(@StringRes stringId: Int, location: Location = BOTTOM) {
        val msg = mContext.getString(stringId)
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_success,
            location
        )
    }

    fun success(msg: CharSequence, location: Location = BOTTOM) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_success,
            location
        )
    }

    fun warn(msg: String, location: Location = BOTTOM) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_warning,
            location
        )
    }

    fun warn(@StringRes stringId: Int, location: Location = BOTTOM) {
        val msg = mContext.getString(stringId)
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_warning,
            location
        )
    }

    fun warn(msg: CharSequence, location: Location = BOTTOM) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_warning,
            location
        )
    }

    fun error(msg: String, location: Location = BOTTOM) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_error,
            location
        )
    }

    fun error(@StringRes stringId: Int, location: Location = BOTTOM) {
        val msg = mContext.getString(stringId)
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_error,
            location
        )
    }

    fun error(msg: CharSequence, location: Location = BOTTOM) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_error,
            location
        )
    }


    private fun cancel() {
        toast?.cancel()
        mToastHandler.removeCallbacksAndMessages(null)
    }

    private fun toastImpl(
        msg: CharSequence,
        duration: Int,
        @DrawableRes drawableId: Int,
        location: Location
    ) {
        toast?.let {
            cancel()
            toast = null
        }
        // 不是主线程使用会报错： java.lang.NoClassDefFoundError: com.sll.lib_framework.util.ToastUtils
        // 需要下面的判断
        if (!Looper.getMainLooper().isCurrentThread) {
            Looper.prepare()
            toast = Toast.makeText(mContext, msg, duration)
            Looper.loop()
        } else {
            toast = Toast.makeText(mContext, msg, duration)
        }
        toast?.show()
//        mToastHandler.postDelayed({
//            try {
//                binding.frameworkTvToastContent.apply {
//                    text = msg
//                    // 设置左侧图标
//                    setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(mContext, drawableId)?.tint(textColor), null, null, null)
//                }
//                toast = Toast(mContext).apply {
//                    view = binding.root
//                    location.let {
//                        setGravity(it.gravity, it.xOffset, it.yOffset)
//                    }
//                    ObjectAnimator.ofFloat(binding.root, "alpha",0f, 1f).apply {
//                        this.duration = 30L
//                        interpolator = FastOutSlowInInterpolator()
//                        start()
//                    }
//                    this.duration = duration
//                    show()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }, 50)
    }

    data class Location(
        val gravity: Int,
        val xOffset: Int,
        val yOffset: Int
    )
}