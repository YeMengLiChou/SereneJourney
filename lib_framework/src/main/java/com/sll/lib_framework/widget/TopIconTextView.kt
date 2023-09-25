package com.sll.lib_framework.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.setPadding
import com.sll.lib_framework.R

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/17
 */
class TopIconTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        private const val TAG = "TopIconTextView"
    }

    // 顶部icon
    private lateinit var mTopIconImageView: ImageFilterView

    val iconView get() = mTopIconImageView

    // 底部文字View
    private lateinit var mBottomTextView: TextView


    // 底部文字
    var text: CharSequence? = null

    private var mIconResId: Int

    var textSize: Float = 0f


    init {
        val dp4 = 4.dp

        orientation = VERTICAL
        initImageView()
        initTextView()
        this.addView(mTopIconImageView, LayoutParams(LayoutParams.WRAP_CONTENT, 0).apply {
            weight = 1f
            gravity = Gravity.CENTER_HORIZONTAL
        })
        this.addView(mBottomTextView, LayoutParams(LayoutParams.MATCH_PARENT, 0).apply {
            weight = 1f
        })
        context.theme.obtainStyledAttributes(attrs, R.styleable.TopIconTextView, 0, 0).apply {
            try {
                text = this.getText(R.styleable.TopIconTextView_android_text).also {
                    mBottomTextView.text = it
                }
                textSize = this.getDimension(R.styleable.TopIconTextView_android_textSize, 14.sp).also {
                    mBottomTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
                }
                mIconResId = this.getResourceId(R.styleable.TopIconTextView_android_icon, 0).also {
                    setIcon(it)
                }
            } finally {
                recycle()
            }
        }


    }

    private fun initImageView() {
        mTopIconImageView = ImageFilterView(context)
        mTopIconImageView.apply {
            setImageResource(mIconResId)
            roundPercent = 1f
            adjustViewBounds = true
            setPadding(2.dp)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    private fun initTextView() {
        mBottomTextView = TextView(context)
        mBottomTextView.apply {
            gravity = Gravity.CENTER

        }
    }



    fun setIcon(@DrawableRes resId: Int) {
        this.mIconResId = resId
        mTopIconImageView.setImageResource(resId)
    }


    fun startIconAnimation(animation: Animation) {
        mTopIconImageView.startAnimation(animation)
    }



    private val Int.dpf: Float
        get() = context.resources.displayMetrics.density * this

    private val Int.dp: Int
        get() = (context.resources.displayMetrics.density * this + 0.5).toInt()

    private val Int.sp: Float
        get() = context.resources.displayMetrics.scaledDensity * this




}