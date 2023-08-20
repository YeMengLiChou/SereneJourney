package com.sll.mod_imageshare.adapter.vh

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sll.lib_framework.base.viewholder.BaseBindViewHolder
import com.sll.mod_image_share.databinding.IsLayoutItemPreviewBinding

/**
 * 预览图片 ViewHolder
 * TODO： 换成可以方法缩小的 View
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/20
 */
class PreviewViewHolder(binding: IsLayoutItemPreviewBinding) : BaseBindViewHolder<IsLayoutItemPreviewBinding>(binding) {
    companion object {
        private const val TAG = "PreviewViewHolder"
    }
}