package com.sll.lib_framework.util

//noinspection SuspiciousImport
import android.R
import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import kotlin.math.abs


/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/11
 */
object ImeHeightUtils {

    // DecorView 前一次不可见高度，用于判断高度变化
    private var mDecorViewInvisibleHeightPre = 0

    // DecorView 在 底部导航栏 不可见的高度
    private var mDecorViewNavDelta = 0

    // 监听
    private val layoutListeners: HashMap<Window, OnGlobalLayoutListener> = HashMap()

    /**
     * 监听接口
     * */
    interface ImeHeightListener {
        fun onImeHeightChanged(height: Int)
    }

    // ========================= 方法一 ==========================
    /**
     * 获取 DecorView 的不可见部分高度
     * */
    private fun getDecorViewInvisibleHeight(window: Window): Int {
        val decorView = window.decorView

        val rect = Rect()
        decorView.getWindowVisibleDisplayFrame(rect)

        val delta = abs(decorView.bottom - rect.bottom)
        // 该部分是包含了底部导航栏的,
        if (delta <= navBarHeight) {
            mDecorViewNavDelta = delta
            return 0
        }
        // 减去在底部导航栏的一部分
        return delta - mDecorViewNavDelta
    }

    /**
     * 软键盘高度监听，对应 [unregisterImeListener]
     * @param activity
     * @param listener
     * */
    fun registerImeListener(window: Window, listener: ImeHeightListener) {
        val flags = window.attributes.flags
        // 不允许扩展到窗口之外
        if (flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        // 获取当前 content view
        val contentView = window.findViewById<FrameLayout>(R.id.content)
        // 不可见高度
        mDecorViewInvisibleHeightPre = getDecorViewInvisibleHeight(window)
        val onGlobalLayoutListener = OnGlobalLayoutListener {
            val height: Int = getDecorViewInvisibleHeight(window)
            if (mDecorViewInvisibleHeightPre != height) {
                listener.onImeHeightChanged(height)
                mDecorViewInvisibleHeightPre = height
            }
        }
        contentView.viewTreeObserver
            .addOnGlobalLayoutListener(onGlobalLayoutListener)
        layoutListeners[window] = onGlobalLayoutListener
    }

    /**
     * 软键盘高度监听，对应 [unregisterImeListener]
     * */
    fun registerImeListener(window: Window, block: (Int) -> Unit) {
        registerImeListener(window, object : ImeHeightListener {
            override fun onImeHeightChanged(height: Int) {
                block(height)
            }
        })
    }

    /**
     * 软键盘高度监听，对应 [unregisterImeListener]
     * */
    fun registerImeListener(activity: Activity, block: (Int) -> Unit) {
        registerImeListener(activity.window, object : ImeHeightListener {
            override fun onImeHeightChanged(height: Int) {
                block(height)
            }
        })
    }

    fun registerItemAnimatedListener(
        window: Window,
        action: (height: Int, fraction: Float, offset: Int) -> Unit
    ) {
        var preValue = 0
        var animator: ValueAnimator? = null
        val animationInterpolator = FastOutLinearInInterpolator()
        registerImeListener(window) {
            animator?.cancel()
            animator = if (it > 0) ValueAnimator.ofInt(0, it)
            else ValueAnimator.ofInt(preValue, 0)
            animator?.apply {
                duration = 200
                interpolator = animationInterpolator
                addUpdateListener {
                    val curValue = it.animatedValue as Int
                    action(curValue, it.animatedFraction, preValue - curValue)
                    preValue = curValue
                }
                start()
            }
        }
    }

    /**
     * 伪监听软键盘的高度变化,建议实现 [onCancel] 用于动画未完成时取消的恢复操作
     * */
    @TargetApi(Build.VERSION_CODES.R)
    inline fun registerItemAnimatedListener(
        window: Window,
        crossinline onStart: () -> Unit = {},
        crossinline onEnd: () -> Unit = {},
        crossinline onCancel: () -> Unit = {},
        crossinline action: (height: Int, fraction: Float, offset: Int) -> Unit
    ) {
        var preValue = 0
        var animator: ValueAnimator? = null
        val animationInterpolator = FastOutLinearInInterpolator()
        registerImeListener(window) {
            animator?.cancel()
            animator =
                if (it > 0) ValueAnimator.ofInt(0, it)
                else ValueAnimator.ofInt(preValue, 0)
            animator?.apply {
                duration = 200
                interpolator = animationInterpolator
                addUpdateListener {
                    val curValue = it.animatedValue as Int
                    action(curValue, it.animatedFraction, preValue - curValue)
                    preValue = curValue
                }
                start()
            }
            animator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    onStart()
                }

                override fun onAnimationEnd(animation: Animator) {
                    onEnd()
                }

                override fun onAnimationCancel(animation: Animator) {
                    onCancel()
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }


    /**
     * 取消监听，对应 [registerImeListener]
     * @param window
     * */
    fun unregisterImeListener(window: Window) {
        val contentView = window.decorView.findViewById<View>(R.id.content) ?: return
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(layoutListeners[window])
    }

    /**
     * 取消监听，对应 [registerImeListener]
     * @param activity
     * */
    fun unregisterImeListener(activity: Activity) {
        unregisterImeListener(activity.window)
    }

    /**
     * 底部导航栏的高度
     * */
    private val navBarHeight: Int
        @SuppressLint("InternalInsetResource", "DiscouragedApi")
        get() {
            val res = Resources.getSystem()
            val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId != 0) {
                return res.getDimensionPixelSize(resourceId)
            } else return 0
        }


    // =========================   方法二  =======================================

    /**
     * 用于测量 Ime 高度的 PopupWindow，测量出来的高度与真实值相差 StatusBar 的高度
     * */
    class ImeHeightMeasure(private val activity: Activity) : PopupWindow(activity) {
        private var mListener: ImeHeightListener? = null

        private val popupView: View?

        private val parentView: View


        init {
            popupView = View(activity.applicationContext).apply { contentView = this }
            // 设置为可以自动调节布局参数
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
            inputMethodMode = INPUT_METHOD_NEEDED
            parentView = activity.findViewById(R.id.content)
            width = 0
            height = WindowManager.LayoutParams.MATCH_PARENT
            // 开始监听
            popupView.viewTreeObserver?.addOnGlobalLayoutListener {
                handleOnGlobalLayout()
            }
        }

        /**
         * 开始监听
         * */
        fun start() {
            parentView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(view: View) {
                    if (!isShowing && parentView.windowToken != null) {
                        setBackgroundDrawable(ColorDrawable(0)) // 设置为透明
                        showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0) // 位置设置为左上角
                    }
                }

                override fun onViewDetachedFromWindow(view: View) {}
            })
        }

        /**
         * 停止监听，建议在 [onDestroy][android.app.Activity.onDestroy] 调用
         * */
        fun close() {
            mListener = null
            dismiss()
        }

        fun setImeHeightListener(listener: ImeHeightListener) {
            mListener = listener
        }

        fun setImeHeightListener(block: (Int) -> Unit) {
            mListener = object : ImeHeightListener {
                override fun onImeHeightChanged(height: Int) {
                    block(height)
                }
            }
        }

        private fun handleOnGlobalLayout() {
            val screenSize = Point()
            activity.windowManager.defaultDisplay.getSize(screenSize)
            val rect = Rect()
            popupView!!.getWindowVisibleDisplayFrame(rect)
            val height = screenSize.y - rect.bottom
            notifyImeHeightChanged(height)
        }

        /**
         * 发生改变，告知监听
         * */
        private fun notifyImeHeightChanged(height: Int) {
            mListener?.onImeHeightChanged(height)
        }
    }


    // ============================= 方法三 ===============================

    /**
     * 添加 Ime 高度改变的回调，这是一个连续变化值
     * */
    @RequiresApi(Build.VERSION_CODES.R)
    inline fun setImeHeightCallback(
        activity: Activity,
        crossinline onAction: (height: Int) -> Unit
    ) {
        val cb = object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
            override fun onProgress(insets: WindowInsets, animations: MutableList<WindowInsetsAnimation>): WindowInsets {
                val posBottom =
                    insets.getInsets(WindowInsets.Type.ime()).bottom + insets.getInsets(WindowInsets.Type.systemBars()).bottom
                onAction.invoke(posBottom)
                return insets
            }
        }
        activity.window.decorView.setWindowInsetsAnimationCallback(cb)
    }

    /**
     * 添加 Ime 高度改变的回调，这是一个连续变化值，包含了高度值以及偏移量，
     * @param activity
     * @param onAction
     * - height 是当前高度值，
     * - fraction 是当前动画的比例值
     * - offset是偏移量，大于0时为弹出软键盘，小于0为收回软键盘
     * */
    @RequiresApi(Build.VERSION_CODES.R)
    inline fun setImeHeightCallback(
        activity: Activity,
        crossinline onAction: (height: Int, fraction: Float, offset: Int) -> Unit
    ) {
        var pre = 0
        var anim: WindowInsetsAnimation? = null
        val cb = object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
            override fun onPrepare(animation: WindowInsetsAnimation) {
                super.onPrepare(animation)
                anim = animation
            }

            override fun onProgress(insets: WindowInsets, animations: MutableList<WindowInsetsAnimation>): WindowInsets {
                val height = insets.getInsets(WindowInsets.Type.ime()).bottom + insets.getInsets(WindowInsets.Type.systemBars()).bottom
                onAction.invoke(height, anim?.fraction ?: 0f, height - pre)
                pre = height
                return insets
            }
        }
        activity.window.decorView.setWindowInsetsAnimationCallback(cb)
    }

    /**
     * 添加 Ime 高度改变的回调，这是一个连续变化值，包含了高度值以及偏移量，
     * @param activity
     * @param onPrepare 动画开始前的一些处理
     * @param onStart 动画准备开始
     * @param onEnd 动画结束
     * @param onProgress height 是当前高度值，offset是偏移量，大于0时为弹出软键盘，小于0为收回软键盘
     * */
    @RequiresApi(Build.VERSION_CODES.R)
    inline fun setImeHeightCallback(
        activity: Activity,
        crossinline onPrepare: (animation: WindowInsetsAnimation) -> Unit = { },
        crossinline onStart: () -> Unit = { },
        crossinline onEnd: () -> Unit = { },
        crossinline onProgress: (height: Int, fraction: Float, offset: Int) -> Unit
    ) {
        setImeHeightCallback(activity.window.decorView, onPrepare, onStart, onEnd, onProgress)
    }

    /**
     * 添加 Ime 高度改变的回调，这是一个连续变化值，包含了高度值以及偏移量，
     * @param activity
     * @param onPrepare 动画开始前的一些处理
     * @param onStart 动画准备开始
     * @param onEnd 动画结束
     * @param onProgress height 是当前高度值，offset是偏移量，大于0时为弹出软键盘，小于0为收回软键盘
     * */
    @RequiresApi(Build.VERSION_CODES.R)
    inline fun setImeHeightCallback(
        view: View,
        crossinline onPrepare: (animation: WindowInsetsAnimation) -> Unit = { },
        crossinline onStart: () -> Unit = { },
        crossinline onEnd: () -> Unit = { },
        crossinline onProgress: (height: Int, fraction: Float, offset: Int) -> Unit
    ) {
        var pre = 0
        var anim: WindowInsetsAnimation? = null
        val cb = object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
            override fun onPrepare(animation: WindowInsetsAnimation) {
                super.onPrepare(animation)
                anim = animation
                onPrepare(animation)
            }

            override fun onStart(animation: WindowInsetsAnimation, bounds: WindowInsetsAnimation.Bounds): WindowInsetsAnimation.Bounds {
                onStart()
                return super.onStart(animation, bounds)
            }

            override fun onEnd(animation: WindowInsetsAnimation) {
                super.onEnd(animation)
                onEnd()
            }

            override fun onProgress(insets: WindowInsets, animations: MutableList<WindowInsetsAnimation>): WindowInsets {
                val height = insets.getInsets(WindowInsets.Type.ime()).bottom + insets.getInsets(WindowInsets.Type.systemBars()).bottom
                onProgress(height, anim?.fraction ?: 0f, height - pre)
                pre = height
                return insets
            }
        }
        view.setWindowInsetsAnimationCallback(cb)
    }


    // =========================== 总的适配方案：方法一+方法二 ==================

    /**
     * 总的适配方案
     * */
    fun setImeHeightListener(activity: Activity, action: (height: Int, fraction: Float, offset: Int) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setImeHeightCallback(activity, action)
        } else {
            registerItemAnimatedListener(activity.window, action)
        }
    }

    /**
     * 总的适配方案
     * */
    inline fun setImeHeightListener(
        activity: Activity,
        crossinline onStart: () -> Unit = {},
        crossinline onEnd: () -> Unit = {},
        crossinline onCancel: () -> Unit = {},
        crossinline action: (height: Int, fraction: Float, offset: Int) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setImeHeightCallback(activity, {}, onStart, onEnd, action)
        } else {
            registerItemAnimatedListener(activity.window, onStart, onEnd, onCancel, action)
        }
    }


}