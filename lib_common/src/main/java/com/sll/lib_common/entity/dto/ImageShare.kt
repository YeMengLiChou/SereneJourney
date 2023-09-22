package com.sll.lib_common.entity.dto

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.squareup.moshi.JsonClass

/**
 * 图片分享实体
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */
@JsonClass(generateAdapter = true)
data class ImageShare (
    // 当前图片分享的用户收藏的主键
    var collectId: String?,
    // 当前图片分享的收藏数
    var collectNum: Int?,
    // 内容
    var content: String,
    // 创建时间
    val createTime: String,
    // 是否已收藏
    var hasCollect: Boolean,
    // 是否已关注
    var hasFocus: Boolean,
    // 是否已点赞
    var hasLike: Boolean,
    // 主键id
    val id: String,
    // 一组图片的唯一标识符
    val imageCode: String,
    // 图片的list集合
    val imageUrlList: List<String>,
    // 当前图片分享的用户点赞的主键id
    var likeId: String?,
    // 当前图片分享的点赞数
    var likeNum: Int?,
    // 发布者id
    val pUserId: String,
    // 标题
    val title: String,
    // 发布者名字
    var username: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    fun update(imageShare: ImageShare) {
        hasCollect = imageShare.hasCollect
        collectNum = imageShare.collectNum
        hasFocus = imageShare.hasFocus
        hasLike = imageShare.hasLike
        likeId = imageShare.likeId
        likeNum = imageShare.likeNum
        imageShare.username?.let { username = it }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(collectId)
        dest.writeValue(collectNum)
        dest.writeString(content)
        dest.writeString(createTime)
        dest.writeByte(if (hasCollect) 1 else 0)
        dest.writeByte(if (hasFocus) 1 else 0)
        dest.writeByte(if (hasLike) 1 else 0)
        dest.writeString(id)
        dest.writeString(imageCode)
        dest.writeStringList(imageUrlList)
        dest.writeString(likeId)
        dest.writeValue(likeNum)
        dest.writeString(pUserId)
        dest.writeString(title)
        dest.writeString(username)
    }

    companion object CREATOR : Creator<ImageShare> {
        override fun createFromParcel(parcel: Parcel): ImageShare {
            return ImageShare(parcel)
        }

        override fun newArray(size: Int): Array<ImageShare?> {
            return arrayOfNulls(size)
        }
    }
}