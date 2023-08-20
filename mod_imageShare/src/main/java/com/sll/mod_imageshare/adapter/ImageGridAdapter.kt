package com.sll.mod_imageshare.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.manager.AppManager
import com.sll.lib_framework.util.debug
import com.sll.mod_image_share.R

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/20
 */
class ImageGridAdapter(
    private val context: Context,
    private val urls: List<String>
): BaseAdapter() {
    companion object {
        private const val TAG = "ImageGridAdapter"
    }
    // 屏幕宽度 - 两侧外边距 - 项之间的边距
    private val columnWidth = (AppManager.screenWidthPx - (16 * 2).dp - (8 * 2).dp) / 3

    private val dp4 = 4.dp

    override fun getCount(): Int = urls.size

    override fun getItem(position: Int) = urls[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val iv = if (convertView != null && convertView is ImageView) {
            convertView
        } else ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = ViewGroup.LayoutParams(columnWidth, columnWidth)
            setClipViewCornerRadius(dp4)
        }
        Glide.with(context).load(getItem(position))
            .error(R.color.is_gray_dim)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(iv)
        return iv
    }
}