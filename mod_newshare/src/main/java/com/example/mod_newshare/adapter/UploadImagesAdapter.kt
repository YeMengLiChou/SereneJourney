package com.example.mod_newshare.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mod_newshare.R

class UploadImagesAdapter(val list:List<Uri>): RecyclerView.Adapter<UploadImagesAdapter.ViewHolder>() {
    companion object{
        private var index: Int = 0
    }
    private lateinit var action: onAdapterAction

    interface onAdapterAction{
        fun delete(index: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_upload_images,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivUpload.setImageURI(list[position])
        holder.ivCancel.setOnClickListener{
            setIndex(position)
            action.delete(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val ivUpload = itemView.findViewById<ImageView>(R.id.iv_upload)
        val ivCancel = itemView.findViewById<ImageView>(R.id.iv_cancel)
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
}