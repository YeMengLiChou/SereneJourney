package com.sll.lib_common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.ext.res.drawable
import com.sll.lib_framework.helper.AppHelper
import com.sll.lib_framework.util.FileUtils
import com.sll.lib_framework.util.debug
import com.sll.lib_glide.transformation.BlendColorTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/18
 */


/**
 * 全模块统一，设置头像
 * */
fun ImageView.setAvatar(url: String?) {
    if (url == null) {
        Glide.with(this).load(R.drawable.common_ic_default_avatar).into(this)
    } else {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.common_ic_default_avatar)
            .fallback(R.drawable.common_ic_default_avatar)
            .placeholder(R.drawable.common_anim_loading) // TODO: 设置一个加载动图
            .signature { md ->
                // 以 更新时间 作为 key 存储缓存
                val time = ServiceManager.settingService.getUserAvatarLastUpdateTime()
                val bytes = time.toString().toByteArray()
                md.update(bytes)
            }.transition(withCrossFade()).into(this)
    }
}

fun Target<Drawable>.setAvatar(context: Context, url: String?) {
    if (url == null) {
        Glide.with(context).load(R.drawable.common_ic_default_avatar).into(this)
    } else {
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.common_ic_default_avatar)
            .fallback(R.drawable.common_ic_default_avatar)
            .placeholder(R.drawable.common_ic_default_avatar) // TODO: 设置一个加载动图
            .signature { md ->
                // 以 更新时间 作为 key 存储缓存
                val time = ServiceManager.settingService.getUserAvatarLastUpdateTime()
                val bytes = time.toString().toByteArray()
                md.update(bytes)
            }.into(this)
    }
}


/**
 * 设置本地图片作为背景图
 * @param uri
 * */
inline fun ImageView.setLocalBackground(
    uri: Uri?,
    crossinline callback: (res: Drawable?) -> Unit = {}
) {
    val a = Glide.with(this).load(uri)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // 保存裁剪过的数据，因为是本地数据
        .transform(BlendColorTransformation(Color.parseColor("#D0D0D0")))
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                // TODO: 恢复原图，或者给一个默认
                return true // 加载失败时不更换原图
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                callback(resource)
                return false
            }
        })
        .into(this)
}

/**
 * 设置本地图片作为背景图
 * @param path
 * */
inline fun ImageView.setLocalBackground(
    path: String?,
    crossinline callback: (res: Drawable?) -> Unit = {}
) {
    setLocalBackground(path?.let { File(it).toUri() }, callback)
}

/**
 * 加载远程图片
 * */
inline fun ImageView.setRemoteBackground(
    url: String?,
    crossinline callback: (Drawable?) -> Unit = {}
) {
    Glide.with(this).load(url)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // 缓存原数据，方便下载
        .transform(BlendColorTransformation(Color.parseColor("#D0D0D0")))
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                return true // 加载失败时不更换原图
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                callback(resource)
                return false
            }
        })
        .into(this)
}


/**
 * 保存指定 url 到本地
 * */
fun downloadOriginPictures(
    context: Context,
    url: String,
    path: String = ServiceManager.settingService.getPicturesSavePath(), // 默认使用保存的
    callback: (uri: Uri?) -> Unit
) {
    // 需要在 IO 线程执行
    CoroutineScope(Dispatchers.IO).launch {
        val bitmap = withContext(Dispatchers.IO) {
            Glide.with(context)
                .load(url)
                .submit()
                .get(10, TimeUnit.SECONDS)
        }.toBitmap()
        callback(
            FileUtils.saveImage(
                context,
                bitmap,
                prefix = "save",
                format = Bitmap.CompressFormat.PNG,
                savePath = path
            )
        )
    }
}

/**
 * 设置远程图片
 *
 * */
 fun ImageView.setRemoteImage(
    url: String,
    crop: Boolean = false
) {
    this.scaleType = ImageView.ScaleType.CENTER_INSIDE
    Glide.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .error(R.drawable.common_holder_error)
        .placeholder(R.drawable.common_anim_loading)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                if (crop) this@setRemoteImage.scaleType = ImageView.ScaleType.CENTER_CROP
                return false
            }
        })
        .into(this)
}