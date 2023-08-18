package com.sll.lib_common.entity.dto

import com.squareup.moshi.JsonClass

/**
 * 用户信息实体
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */
@JsonClass(generateAdapter = true)
data class User(
    // 用户创建应用的id
    val appKey: String,
    // 头像
    val avatar: String?,
    // 创建时间
    val createTime: Long,
    // 主键id
    val id: Long,
    // 个人介绍
    val introduce: String?,
    // 修改时间
    val lastUpdateTime: Long,
    // 密码
    val password: String?,
    // 性别
    val sex: Int?,
    // 用户名
    val username: String
)