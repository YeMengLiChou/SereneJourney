package com.sll.mod_imageshare.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_common.setRemoteImage
import com.sll.lib_framework.base.viewholder.BaseBindViewHolder
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.fromHtml
import com.sll.lib_framework.ext.res.color
import com.sll.lib_framework.ext.res.drawable
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.manager.AppManager
import com.sll.lib_framework.util.ToastUtils
import com.sll.lib_framework.util.debug
import com.sll.mod_image_share.R
import com.sll.mod_image_share.databinding.IsLayoutItemImageShareBinding
import com.sll.mod_imageshare.repository.ImageRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
class DiscoverAdapter(
    private val context: Context,
    private val scope: LifecycleCoroutineScope,
    private val click: (view: ImageView, item: ImageShare, position: Int) -> Unit
) : PagingDataAdapter<ImageShare, DiscoverAdapter.DiscoverViewHolder>(comparator) {
    companion object {
        private const val TAG = "DiscoverAdapter"

        private val comparator = object : DiffUtil.ItemCallback<ImageShare>() {
            override fun areItemsTheSame(oldItem: ImageShare, newItem: ImageShare): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ImageShare, newItem: ImageShare): Boolean {
                return oldItem == newItem
            }
        }
        private const val MESSAGE_DELAYED = 123
        private const val TIME_DELAYED = 300L
    }

    // 屏幕宽度 - 两侧外边距 - 项之间的边距
    private val columnWidth = (AppManager.screenWidthPx - (16 * 2).dp - (8 * 2).dp) / 3

    private val dp8 = 8.dp

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

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

    override fun onBindViewHolder(holder: DiscoverViewHolder, position: Int) {

        val item = getItem(position) ?: return
        holder.binding.apply {
            // 内容
            isTvContent.text = item.content
            isTvTitle.text = item.title
            isTvPublisherName.text = item.username
            isTvPublishTime.text = parseTimestamp(item.createTime.toLong())

            // 该视图可能是复用的，需要清除已有的图片
            val size = item.imageUrlList.size
            if (isFlowImages.referencedIds?.isNotEmpty() == true) {
                for (id in isFlowImages.referencedIds) {
                    root.removeView(root.findViewById(id))
                }
                isFlowImages.referencedIds = intArrayOf()
            }
            val ids = IntArray(size)
            for (i in 0 until size) {
                ids[i] = initImageView(item.imageUrlList[i], root,  position, i)
            }
            isFlowImages.referencedIds = ids


            /**
             * TODO: Bug 快速点击点赞或收藏会导致状态不同步，尚未找出原因
             *
             * */
            // 关注点击
            switchFocusStyle(item.hasFocus, isBtFocus)
            isBtFocus.setOnClickListener(object : View.OnClickListener {
                var isFocus = item.hasFocus

                // 处理收藏的网络请求
                val handler = object : Handler(Looper.myLooper() ?: Looper.getMainLooper()) {
                    override fun handleMessage(msg: Message) {
                        scope.launch {
                            // 改变了才更新
                            if (isFocus && !item.hasFocus) {
                                ImageRepository.focus(item.pUserId).collect { res ->
                                    res.onSuccess {
                                        refresh() // 可能存在多个相同用户，需要全部刷新
                                        with("成功关注 <b>${item.username}<b>") {
                                            this.fromHtml()
                                                ?.let { it1 -> ToastUtils.success(it1) }
                                                ?: ToastUtils.success(this)
                                        }
                                    }.onError {
                                        ToastUtils.warn("关注失败:${it.message}")
                                        isFocus = item.hasFocus
                                        switchFocusStyle(item.hasFocus, isBtFocus)
                                    }
                                }
                            }
                            if (!isFocus && item.hasFocus) {
                                ImageRepository.cancelFocus(item.pUserId).collect { res ->
                                    res.onSuccess {
                                        refresh() // 可能存在多个相同用户，需要全部刷新
                                        with("已取消关注 <b>${item.username}<b>") {
                                            this.fromHtml()
                                                ?.let { it1 -> ToastUtils.success(it1) }
                                                ?: ToastUtils.success(this)
                                        }
                                    }.onError {
                                        ToastUtils.warn("取消关注失败:${it.message}")
                                        isFocus = item.hasFocus
                                        switchFocusStyle(item.hasFocus, isBtFocus)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onClick(v: View?) {
                    // 切换关注
                    isFocus = !isFocus
                    switchFocusStyle(isFocus, isBtFocus)
                    if (ServiceManager.loginService.isLogin()) { // 先切换图标，延迟发送网络请求，防止多次点击
                        handler.removeMessages(100)
                        handler.sendEmptyMessageDelayed(100, 500L) // 延迟发送
                    } else {
                        ToastUtils.warn("请先登录喔~")
                    }
                }
            })

            // 收藏点击
            switchCollectStyle(item.hasCollect, item, isBtCollect)
            isBtCollect.setOnClickListener(object : View.OnClickListener {
                var isCollect = item.hasCollect

                // 处理收藏的网络请求
                val handler = object : Handler(Looper.myLooper() ?: Looper.getMainLooper()) {
                    override fun handleMessage(msg: Message) {
                        scope.launch {
                            // 改变了才更新
                            if (isCollect && !item.hasCollect) {
                                ImageRepository.collectImageShare(item.id).collect { res ->
                                    res.onSuccess {
                                        scope.launch { ImageRepository.updateImageShareDetail(item) } // 更新数据
                                    }.onError {
                                        ToastUtils.warn("收藏失败:${it.message}")
                                        isCollect = item.hasCollect
                                        item.collectNum -= 1
                                        switchCollectStyle(item.hasCollect, item, isBtCollect)
                                    }
                                }
                            }
                            if (!isCollect && item.hasCollect) {
                                ImageRepository.cancelLikeImageShare(item.id).collect { res ->
                                    res.onSuccess {
                                        scope.launch { ImageRepository.updateImageShareDetail(item) } // 更新数据
                                    }.onError {
                                        ToastUtils.warn("取消收藏失败:${it.message}")
                                        isCollect = item.hasCollect
                                        item.collectNum += 1
                                        switchCollectStyle(item.hasCollect, item, isBtCollect)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onClick(v: View?) {
                    isCollect = !isCollect // 切换点赞
                    item.collectNum = if (isCollect) item.collectNum + 1 else item.collectNum - 1
                    switchCollectStyle(isCollect, item, isBtCollect)
                    if (ServiceManager.loginService.isLogin()) { // 先切换图标，延迟发送网络请求，防止多次点击
                        handler.removeMessages(100)
                        handler.sendEmptyMessageDelayed(100, 500L) // 延迟发送
                    } else {
                        ToastUtils.warn("请先登录喔~")
                    }
                }
            })

            // 点赞点击
            switchLikeStyle(item.hasLike, item, isBtLike)
            isBtLike.setOnClickListener(object : View.OnClickListener {
                var isLiked = item.hasLike

                // 处理点赞的网络请求
                val handler = object : Handler(Looper.myLooper() ?: Looper.getMainLooper()) {
                    override fun handleMessage(msg: Message) {
                        scope.launch {
                            // 改变了才更新
                            if (isLiked && !item.hasLike) {
                                ImageRepository.likeImageShare(item.id).collect { res ->
                                    res.onSuccess {
                                        scope.launch {
                                            ImageRepository.updateImageShareDetail(item)
                                        } // 更新数据
                                    }.onError { // 失败后恢复原状
                                        ToastUtils.warn("点赞失败:${it.message}")
                                        isLiked = item.hasLike
                                        item.likeId = null
                                        item.likeNum -= 1
                                        switchLikeStyle(item.hasLike, item, isBtLike)
                                    }
                                }
                            }
                            if (!isLiked && item.hasLike) {
                                ImageRepository.cancelLikeImageShare(item.likeId!!).collect { res ->
                                    res.onSuccess {
                                        scope.launch {
                                            ImageRepository.updateImageShareDetail(item)
                                        } // 更新数据
                                    }.onError {
                                        ToastUtils.warn("取消点赞失败:${it.message}")
                                        isLiked = item.hasLike
                                        item.likeNum += 1
                                        switchLikeStyle(item.hasLike, item, isBtLike)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onClick(v: View?) {
                    isLiked = !isLiked // 切换点赞
                    item.likeNum = if (isLiked) item.likeNum + 1 else item.likeNum - 1
                    switchLikeStyle(isLiked, item, isBtLike)
                    if (ServiceManager.loginService.isLogin()) { // 先切换图标，延迟发送网络请求，防止多次点击
                        handler.removeMessages(100)
                        handler.sendEmptyMessageDelayed(100, 500L) // 延迟发送
                    } else {
                        ToastUtils.warn("请先登录喔~")
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverViewHolder {
        return DiscoverViewHolder(
            IsLayoutItemImageShareBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    /**
     * 对每个 url 设置一个 ImageView
     * @param url
     * @param layout
     * @param position
     * @param urlPosition
     * */
    private fun initImageView(url: String, layout: ConstraintLayout, position: Int, urlPosition: Int): Int {
        val iv = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = ViewGroup.LayoutParams(columnWidth, columnWidth) // 大小
            setClipViewCornerRadius(dp8) // 设置圆角
            id = View.generateViewId() // 生成一个 id，与 Flow 绑定
            foreground = ContextCompat.getDrawable(context, R.drawable.is_ripple_imageview_pressed)
            isClickable = true
            isFocusable = true
            elevation = 3F // 给一部分阴影
            click { // 点击后触发外部的点击事件
                click.invoke(it as ImageView, getItem(position)!!, urlPosition)
            }
            setRemoteImage(url) // 加载 url
            layout.addView(this) // 加入布局
        }
        return iv.id
    }

    // 切换点赞的样式
    private fun switchLikeStyle(isLiked: Boolean, item: ImageShare, bt: MaterialButton) {
        if (isLiked) {
            bt.icon = context.drawable(R.drawable.is_ic_like_fill)
            bt.setTextColor(context.color(R.color.is_blue_cornflower))
        } else {
            bt.icon = context.drawable(R.drawable.is_ic_like)
            bt.setTextColor(context.color(R.color.is_gray_dim))
        }
        bt.text = item.likeNum.toString()
    }

    // 切换收藏的样式
    private fun switchCollectStyle(isCollected: Boolean, item: ImageShare, bt: MaterialButton) {
        if (isCollected) {
            bt.icon = context.drawable(R.drawable.is_ic_collect_fill)
            bt.setTextColor(context.color(R.color.is_blue_cornflower))
        } else {
            bt.icon = context.drawable(R.drawable.is_ic_collect)
            bt.setTextColor(context.color(R.color.is_gray_dim))
        }
        bt.text = item.likeNum.toString()
    }


    private fun switchFocusStyle(isFocus: Boolean, bt: MaterialButton) {
        if (isFocus) {
            bt.text = "已关注"
            bt.setBackgroundColor(context.color(R.color.is_blue_cornflower))
            bt.setTextColor(context.color(R.color.is_white))
        } else {
            bt.text = "关注"
            bt.setBackgroundColor(context.color(R.color.is_white))
            bt.setTextColor(context.color(R.color.is_blue_cornflower))
        }
    }


    class DiscoverViewHolder(binding: IsLayoutItemImageShareBinding) : BaseBindViewHolder<IsLayoutItemImageShareBinding>(binding)
}