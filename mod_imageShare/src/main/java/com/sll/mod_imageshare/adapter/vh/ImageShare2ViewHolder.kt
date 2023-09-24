package com.sll.mod_imageshare.adapter.vh

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.MainThread
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LifecycleCoroutineScope
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.setAvatar
import com.sll.lib_common.setRemoteImage
import com.sll.lib_framework.base.viewholder.BaseBindViewHolder
import com.sll.lib_framework.ext.As
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.res.color
import com.sll.lib_framework.ext.res.drawable
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.height
import com.sll.lib_framework.ext.view.margin
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.ext.view.visible
import com.sll.lib_framework.ext.view.width
import com.sll.lib_framework.manager.AppManager
import com.sll.lib_framework.util.ToastUtils
import com.sll.mod_image_share.R
import com.sll.mod_image_share.databinding.IsLayoutItemImageSharePreviewBinding
import com.sll.mod_imageshare.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/23
 */
class ImageShare2ViewHolder(
    binding: IsLayoutItemImageSharePreviewBinding,
    private val scope: LifecycleCoroutineScope
) : BaseBindViewHolder<IsLayoutItemImageSharePreviewBinding>(binding) {
    companion object {
        private const val TAG = "ImageShare2ViewHolder"

    }

    private val context = binding.root.context

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    // 是否已经关注（接口返回的数据不准确）
    private var mIsForceFocused = false

    // 是否已经收藏（接口返回的数据不准确）
    private var mIsForceCollected = false

    private var mOnItemActionListener: OnItemActionListener? = null

    private val mLikeExpandAnimator = ValueAnimator.ofFloat(1f, 1.2f).apply {
        addUpdateListener {
            val value = it.animatedValue as Float
            binding.includeLike.ivIcon.scaleX = value
            binding.includeLike.ivIcon.scaleY = value
            Log.i(TAG, "update: $value")
        }
        duration = 200
        interpolator = OvershootInterpolator(1.2f)
        doOnEnd {
            binding.includeLike.ivIcon.setImageResource(R.drawable.is_ic_like_fill)
            mLikeShrinkAnimator.start()
        }
    }

    private val mLikeShrinkAnimator = ValueAnimator.ofFloat(1.2f, 1f).apply {
        addUpdateListener {
            val value = it.animatedValue as Float
            binding.includeLike.ivIcon.scaleX = value
            binding.includeLike.ivIcon.scaleY = value
        }
        duration = 100
        start()
    }

    /**
     * 该 ViewHolder 点击监听，用于监听整体的点击以及ImageView的点击
     * */
    interface OnItemActionListener {
        /**
         * ImageView 的点击
         * @param imageView
         * @param item 对应的ImageShare
         * @param position 对应第几个ImageView
         * */
        fun onImageViewClick(imageView: ImageView, item: ImageShare, position: Int)

        /**
         * 当前 Item 点击时
         * */
        fun onItemClick(view: View)

        /**
         * 需要刷新
         * */
        fun onNeedRefresh()

        fun onCommentClick()
    }

    /**
     * 解析时间戳并格式化为年月日时分的字符串
     *
     * @param timestamp 时间戳
     * @return 格式化后的时间字符串，例如：2023-07-19 14:30
     */
    private fun parseTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        return dateFormat.format(date)
    }

    fun setOnItemActionListener(listener: OnItemActionListener): ImageShare2ViewHolder {
        this.mOnItemActionListener = listener
        return this
    }

    fun setForceFocus(focus: Boolean): ImageShare2ViewHolder {
//        this.mIsForceFocused = focus
        return this
    }

    fun setForceCollect(collect: Boolean): ImageShare2ViewHolder {
        this.mIsForceCollected = collect
        return this
    }


    fun bindData(item: ImageShare) {
        binding.apply {
            root.click {
                mOnItemActionListener?.onItemClick(it)
            }
            tvTitle.text = item.title
            tvContent.text = item.content
            includeUserProfile.tvTime.text = parseTimestamp(item.createTime.toLong())

            // 加载用户信息
            scope.launch(Dispatchers.IO) {
                ImageRepository.requestResponse {
                    ImageRepository.getUserByName(item.username!!)
                }.collect { res ->
                    res.onSuccess {
                        launch(Dispatchers.Main) {
                            loadUserInfo(it, item)
                        }
                    }
                }
            }

            // 加载图片信息
            loadImages(item)
            // 加载底部图文分享信息
            loadBottomBar(item)
        }
    }


    /**
     * 加载用户信息
     * */

    @MainThread
    private fun loadUserInfo(info: User?, item: ImageShare) {
        binding.includeUserProfile.apply {
            ivUserIcon.setAvatar(info?.avatar)
            tvUsername.text = info?.username ?: ""
            when (info?.sex) {
                1 -> {
                    ivUserSex.visible()
                    ivUserSex.setImageResource(R.drawable.is_ic_man)
                }

                2 -> {
                    ivUserSex.visible()
                    ivUserSex.setImageResource(R.drawable.is_ic_woman)
                }

                else -> {
                    ivUserSex.gone()
                }
            }
            binding.includeUserProfile.btFocus.setTag(R.id.is_tag_focus, null)
            if (mIsForceFocused || item.hasFocus) {
                setFocusStyle(true)
            } else {
                setFocusStyle(false)
            }

            btFocus.throttleClick {
                val preFocus = it.getTag(R.id.is_tag_focus)?.As<Boolean>() ?: false
                scope.launch(Dispatchers.IO) {
                    if (preFocus) {
                        item.hasFocus = false
                        setFocusStyle(false)
                        // 取消关注
                        ImageRepository.cancelFocus(item.pUserId).collect { res ->
                            res.onSuccess {
                                // 同步状态
                                setFocusStyle(false)
                                mOnItemActionListener?.onNeedRefresh()
                            }.onError {
                                ToastUtils.error("取消关注失败，请检查网络~")
                                // 恢复原状
                                setFocusStyle(true)
                                item.hasFocus = true
                            }
                        }

                    } else {
                        // 点赞
                        item.hasFocus = true
                        setFocusStyle(true)
                        ImageRepository.focus(item.pUserId).collect { res ->
                            res.onSuccess {
                                setFocusStyle(true)
                                mOnItemActionListener?.onNeedRefresh()
                            }.onError {
                                ToastUtils.error("关注失败，请检查网络~")
                                setFocusStyle(false)
                                item.hasFocus = false
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 加载图片信息，最多两个图片
     * */
    private fun loadImages(item: ImageShare) {
        val dp8 = 8.dp
        binding.frameLayoutImages.setClipViewCornerRadius(dp8)
        // 计算当前位置的大小
        val width =
            (binding.root.width - dp8 - binding.root.paddingStart * 2).takeIf { it > 0 } ?: (AppManager.screenWidthPx - dp8 * 5 )
        val height = width / 2

        // url的长度
        val imageSize = item.imageUrlList.size
        // 移除所有imageView
        binding.linearImages.removeAllViews()
        if (imageSize == 0) {
            binding.frameLayoutImages.gone()
        } else {
            // 加载图片
            binding.frameLayoutImages.height(height).width(width + dp8).visible()

            if (imageSize == 1) {
                addImageViewToLinearLayout(width, height, item.imageUrlList[0], 0, item)
            } else {
                addImageViewToLinearLayout(width / 2, height, item.imageUrlList[0], 0, item)
                addImageViewToLinearLayout(width / 2, height, item.imageUrlList[1], 1, item).apply {
                    margin(left = dp8)
                }
            }
            // 显示更多图片的标志
            if (imageSize == 2) {
                binding.tvMoreImages.gone()
            } else {
                // 设置标志
                binding.tvMoreImages.visible()
                binding.tvMoreImages.setClipViewCornerRadius(dp8)
                binding.tvMoreImages.text = "+${imageSize}"
            }
        }
    }

    /**
     * 根据不同的需要获取 ImageView
     * */
    private fun addImageViewToLinearLayout(width: Int, height: Int, url: String, position: Int, item: ImageShare): ImageView {
        Log.i(TAG, "addImageViewToLinearLayout: ${width}, ${height}, $position")
        val view = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            click {
                // 点击
                mOnItemActionListener?.onImageViewClick(it as ImageView, item, position)
            }
            setBackgroundColor(Color.WHITE) // 设置白色背景
            isClickable = true
            isFocusable = true
            elevation = 3F // 给一部分阴影
            foreground = context.drawable(R.drawable.is_ripple_imageview_pressed)
        }
        view.setRemoteImage(url, true)
        binding.linearImages.addView(view, LinearLayout.LayoutParams(width, height))
        return view
    }

    /**
     * 加载底部
     * */
    private fun loadBottomBar(item: ImageShare) {
        binding.includeLike.root.setTag(R.id.is_tag_like, null)

        // 点赞
        binding.includeLike.apply {
            setLikeStyle(item.hasLike, item.likeNum ?: 0, false)
            root.throttleClick {
                val preLike = binding.includeLike.root.getTag(R.id.is_tag_like)?.As<Boolean>() ?: false
                val preLikeNum = item.likeNum ?: 0
                var curLikeNum: Int

                scope.launch(Dispatchers.IO) {
                    if (preLike) {
                        curLikeNum = preLikeNum - 1
                        item.likeNum = curLikeNum
                        setLikeStyle(false, likeCount = curLikeNum, false)
                        if (item.likeId == null) {
                            setLikeStyle(false, likeCount = curLikeNum, false)
                        } else {
                            // 取消点赞
                            ImageRepository.cancelLikeImageShare(item.likeId!!).collect { res ->
                                res.onSuccess {
                                    // 同步状态
                                    setLikeStyle(false, likeCount = curLikeNum, false)
                                }.onError {
                                    ToastUtils.error("取消点赞失败，请检查网络~")
                                    // 恢复原状
                                    setLikeStyle(preLike, preLikeNum, false)
                                    item.likeNum = preLikeNum
                                }
                            }
                        }
                    } else { // 点赞
                        curLikeNum = preLikeNum + 1
                        item.likeNum = curLikeNum
                        setLikeStyle(true, curLikeNum, true)
                        ImageRepository.likeImageShare(item.id).collect { res ->
                            res.onSuccess {
                                setLikeStyle(true, curLikeNum, false)
                                // 更新 likeId
                                scope.launch(Dispatchers.IO) {
                                    ImageRepository.updateImageShareDetail(item)
                                }
                            }.onError {
                                ToastUtils.error("点赞失败，请检查网络~")
                                setLikeStyle(preLike, preLikeNum, false)
                                item.likeNum = preLikeNum
                            }
                        }
                    }
                }
            }
        }

        binding.includeComment.apply {
            ivIcon.setImageResource(R.drawable.is_ic_comment)
            tvText.text = "评论"
            root.click {
                mOnItemActionListener?.onCommentClick()
            }
        }
    }


    private fun setLikeStyle(liked: Boolean, likeCount: Int, animated: Boolean) {
        scope.launch(Dispatchers.Main) {
            binding.includeLike.apply {
                val preLiked = root.getTag(R.id.is_tag_like)?.As<Boolean>()
                // 初始化 TAG
                if (preLiked == null) {
                    if (liked) {
                        ivIcon.setImageResource(R.drawable.is_ic_like_fill)
                        root.setTag(R.id.is_tag_like, true)
                    } else {
                        ivIcon.setImageResource(R.drawable.is_ic_like)
                        root.setTag(R.id.is_tag_like, false)
                    }
                    tvText.text = if (likeCount == 0) "赞" else likeCount.toString()
                } else {
                    // 没有点赞且需要点赞
                    if (!preLiked && liked) {
                        if (animated) {
                            mLikeExpandAnimator.start()
                        } else {
                            ivIcon.setImageResource(R.drawable.is_ic_like_fill)
                        }
                        root.setTag(R.id.is_tag_like, liked)
                        tvText.text = likeCount.toString()
                    }
                    if (preLiked && !liked) {
                        root.setTag(R.id.is_tag_like, liked)
                        ivIcon.setImageResource(R.drawable.is_ic_like)
                        tvText.text = if (likeCount == 0) "赞" else likeCount.toString()
                    }
                }
            }
        }
    }

    private fun setFocusStyle(focused: Boolean) {
        scope.launch(Dispatchers.Main) {
            binding.includeUserProfile.btFocus.apply {
                val preFocus = getTag(R.id.is_tag_focus)?.As<Boolean>()
                // 没有点赞且需要点赞
                if ((preFocus == null || !preFocus) && focused) {
                    setTag(R.id.is_tag_focus, true)
                    setIconResource(0)
                    text = "已关注"
                    setTextColor(context.color(R.color.is_blue_cornflower))
                    backgroundTintList = ColorStateList.valueOf(context.color(R.color.is_blue_alice))
                }
                if ((preFocus == null || preFocus) && !focused) {
                    setIconResource(R.drawable.is_ic_plus)
                    setTag(R.id.is_tag_focus, false)
                    text = "关注"
                    setTextColor(context.color(R.color.is_white))
                    backgroundTintList = ColorStateList.valueOf(context.color(R.color.is_blue_cornflower))
                }
            }
        }
    }

}