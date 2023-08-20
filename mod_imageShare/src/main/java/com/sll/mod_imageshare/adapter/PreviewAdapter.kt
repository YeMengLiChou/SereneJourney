package com.sll.mod_imageshare.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sll.lib_common.setRemoteImage
import com.sll.lib_framework.ext.res.layoutInflater
import com.sll.mod_image_share.R
import com.sll.mod_image_share.databinding.IsLayoutItemPreviewBinding
import com.sll.mod_imageshare.adapter.vh.PreviewViewHolder

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/20
 */
class PreviewAdapter(
    private val context: Context
): ListAdapter<String, PreviewViewHolder>(diffCallback) {
    companion object {
        private const val TAG = "PreviewAdapter"
        // 差分
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewViewHolder {
        return PreviewViewHolder(
            IsLayoutItemPreviewBinding.inflate(context.layoutInflater, parent, false)
        )
    }

    // 加载图片
    override fun onBindViewHolder(holder: PreviewViewHolder, position: Int) {
        holder.binding.apply {
            isIvPreview.setRemoteImage(getItem(position))
        }
    }
}