package com.sll.mod_imageshare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_framework.base.viewholder.BaseBindViewHolder
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.manager.AppManager
import com.sll.mod_image_share.R
import com.sll.mod_image_share.databinding.IsLayoutItemImageShareBinding
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
    private val context: Context
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
    }

    // 屏幕宽度 - 两侧外边距 - 项之间的边距
    private val columnWidth = (AppManager.screenWidthPx - (16 * 2).dp - (8 * 2).dp) / 3

    private val dp4 = 4.dp

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
            isTitle.text = item.title
            // 内容
            isContent.text = item.content
            isPublisherName.text = item.username
            isPublishTime.text = parseTimestamp(item.createTime.toLong())

            // 该视图可能是复用的，需要清除已有的图片
            val size = item.imageUrlList.size
            if (isImages.referencedIds?.isNotEmpty() == true) {
                for (id in isImages.referencedIds) {
                    root.removeView(root.findViewById(id))
                }
                isImages.referencedIds = intArrayOf()
            }

            val ids = IntArray(size)
            for (i in 0 until size) {
                ids[i] = initImageView(item.imageUrlList[i], root)
            }
            isImages.referencedIds = ids
            // 关注
            if (item.hasFocus) {
                // TODO 填充
                isFocus.text = "已关注"
            }
            // 收藏
            isCollectNumber.text = item.collectNum.toString()
            if (item.hasCollect) {
                // TODO 填充
            }

            isLikeNumber.text = item.likeNum.toString()
            if (item.hasLike) {
                // TODO 填充
            }
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

    private fun initImageView(url: String, layout: ConstraintLayout): Int {
        val iv = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = ViewGroup.LayoutParams(columnWidth, columnWidth)
            setClipViewCornerRadius(dp4)
            id = View.generateViewId()
            background = ContextCompat.getDrawable(context, R.drawable.is_ripple_imageview_pressed)
            isClickable = true
            isFocusable = true
            layout.addView(this)
        }
        Glide.with(context).load(url)
            .error(R.color.is_gray_dim)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(iv)
        return iv.id
    }

    class DiscoverViewHolder(binding: IsLayoutItemImageShareBinding) : BaseBindViewHolder<IsLayoutItemImageShareBinding>(binding)
}