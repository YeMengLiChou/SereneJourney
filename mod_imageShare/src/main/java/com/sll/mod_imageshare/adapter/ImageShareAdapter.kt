package com.sll.mod_imageshare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.service.ServiceManager
import com.sll.mod_image_share.databinding.IsLayoutItemImageShareBinding
import com.sll.mod_image_share.databinding.IsLayoutItemImageSharePreviewBinding
import com.sll.mod_imageshare.adapter.vh.ImageShare2ViewHolder
import com.sll.mod_imageshare.adapter.vh.ImageShareViewHolder

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
class ImageShareAdapter(
    private val context: Context,
    private val scope: LifecycleCoroutineScope,
    private val click: (view: ImageView, item: ImageShare, position: Int) -> Unit
) : PagingDataAdapter<ImageShare, ImageShare2ViewHolder>(comparator) {
    companion object {
        private const val TAG = "ImageShareAdapter"

        private val comparator = object : DiffUtil.ItemCallback<ImageShare>() {
            override fun areItemsTheSame(oldItem: ImageShare, newItem: ImageShare): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ImageShare, newItem: ImageShare): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var needShowFocus = true

    fun setFocus(focus: Boolean) {
        this.needShowFocus = focus
    }

    override fun onBindViewHolder(holder: ImageShare2ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder
            .setForceFocus(needShowFocus)
            .setOnItemActionListener(object : ImageShare2ViewHolder.OnItemActionListener {
                override fun onImageViewClick(imageView: ImageView, item: ImageShare, position: Int) {
                    click.invoke(imageView, item, position)
                }

                override fun onItemClick(view: View) {
                    ServiceManager.detailService.navigate(context, item, false)
                }

                override fun onNeedRefresh() {
                    refresh()
                }

                override fun onCommentClick() {
                    ServiceManager.detailService.navigate(context, item, true)

                }
            }).bindData(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageShare2ViewHolder {
        return ImageShare2ViewHolder(
            IsLayoutItemImageSharePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            scope,
        )
    }

}