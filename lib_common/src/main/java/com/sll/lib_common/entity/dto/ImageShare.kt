package com.sll.lib_common.entity.dto

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
    val collectId: String?,
    // 当前图片分享的收藏数
    val collectNum: Int,
    // 内容
    val content: String,
    // 创建时间
    val createTime: String,
    // 是否已收藏
    val hasCollect: Boolean,
    // 是否已关注
    val hasFocus: Boolean,
    // 是否已点赞
    val hasLike: Boolean,
    // 主键id
    val id: String,
    // 一组图片的唯一标识符
    val imageCode: String,
    // 图片的list集合
    val imageUrlList: List<String>,
    // 当前图片分享的用户点赞的主键id
    val likeId: String?,
    // 当前图片分享的点赞数
    val likeNum: Int,
    // 发布者id
    val pUserId: String,
    // 标题
    val title: String,
    // 发布者名字
    val username: String
)