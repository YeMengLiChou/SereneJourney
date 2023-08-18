package com.sll.lib_common.entity.dto

import com.squareup.moshi.JsonClass

/**
 * 评论实体
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */
@JsonClass(generateAdapter = true)
data class Comment(
    // 所申请的应用key
    val appKey: String,
    // 评论等级[1 一级评论 默认，2 二级评论]
    val commentLevel: Int,
    // 评论的内容
    val content: String,
    // 创建时间
    val createTime: String,
    // 主键id
    val id: Int,
    // 评论人userId
    val pUserId: Int,
    // 父评论id
    val parentCommentId: Int,
    // 父评论的用户id
    val parentCommentUserId: Int,
    // 被回复的评论id
    val replyCommentId: Int,
    // 被回复的评论用户id
    val replyCommentUserId: Int,
    // 图文分享的主键id
    val shareId: Int,
    // 评论人用户名
    val userName: String
)