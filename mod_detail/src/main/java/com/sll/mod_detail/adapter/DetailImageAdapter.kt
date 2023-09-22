package com.sll.mod_detail.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.sll.lib_common.setRemoteImage
import com.sll.lib_framework.base.adapter.BaseRecyclerAdapter
import com.sll.lib_framework.base.viewholder.BaseBindViewHolder
import com.sll.lib_framework.base.viewholder.BaseViewHolder
import com.sll.lib_framework.ext.As
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.view.height
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.ext.view.width
import com.sll.mod_detail.databinding.DetailItemImageBinding

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/19
 */
class DetailImageAdapter(
    context: Context,
    data: MutableList<String>
): BaseRecyclerAdapter<DetailItemImageBinding, String>(context, data) {
    companion object {
        private const val TAG = "DetailImageAdapter"
    }

    // 列数
    private val spanCount: Int

    // 宽度
    var width = 0

    // 子项之间的
    var marginSpan = 0
    init {
        val size = data.size
        spanCount = when {
            size < 3 -> size
            size == 4 -> 2
            else -> 3
        }
    }

    private val imageSize get() = (width - spanCount * marginSpan) / spanCount
    override fun bindData(holder: BaseViewHolder, item: String, position: Int) {
        holder.As<BaseBindViewHolder<DetailItemImageBinding>>()?.apply {
            binding.root.apply {
                elevation = 3f
                width(imageSize).height(imageSize)
                setClipViewCornerRadius(8.dp)
            }

            binding.root.setRemoteImage(item)
        }
    }

    override fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): DetailItemImageBinding {
        return DetailItemImageBinding.inflate(layoutInflater, parent, false)
    }
}