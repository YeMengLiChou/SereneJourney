package com.example.mod_newshare.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mod_newshare.R
import com.example.mod_newshare.databinding.NewshareItemUploadImagesBinding
import com.sll.lib_framework.base.adapter.BaseRecyclerAdapter
import com.sll.lib_framework.base.viewholder.BaseBindViewHolder
import com.sll.lib_framework.base.viewholder.BaseViewHolder
import com.sll.lib_framework.ext.As
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.setClipViewCornerRadius

class UploadImagesAdapter(
    context: Context,
    list:List<Uri>
): BaseRecyclerAdapter<NewshareItemUploadImagesBinding, Uri>(context, list.toMutableList()) {
    companion object{
        private var index: Int = 0
    }
    private lateinit var action: onAdapterAction

    interface onAdapterAction {
        fun delete(index: Int)
    }

    override fun bindData(holder: BaseViewHolder, item: Uri, position: Int) {
        holder.As<BaseBindViewHolder<NewshareItemUploadImagesBinding>>()?.binding?.apply {
            ivUpload.setImageURI(item)
            ivCancel.click {
                setIndex(position)
                action.delete(position)
            }
        }
    }

    override fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): NewshareItemUploadImagesBinding {
        return NewshareItemUploadImagesBinding.inflate(layoutInflater, parent, false).apply {
            ivCancel.setClipViewCornerRadius(8.dp)
            root.setClipViewCornerRadius(8.dp)
        }
    }

    fun setAction(action: onAdapterAction):UploadImagesAdapter{
        this.action = action
        return this
    }

    fun setIndex(index: Int){
        UploadImagesAdapter.index = index
    }
    fun getIndex():Int{
        return UploadImagesAdapter.index
    }
    inner class ViewHolder(binding: NewshareItemUploadImagesBinding) : BaseBindViewHolder<NewshareItemUploadImagesBinding>(binding)
}
