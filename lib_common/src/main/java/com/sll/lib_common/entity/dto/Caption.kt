package com.sll.lib_common.entity.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/10
 */
@JsonClass(generateAdapter = true)
data class Caption(
    val id: Int,
    val length: Int,
    val reviewer: Int,
    val type: String,
    val uuid: String,
    @Json(name = "commit_from") val commitFrom: String,
    @Json(name = "created_at") val createdAt: String,
    val creator: String,
    @Json(name = "creator_uid") val creatorUid: Int,
    val from: String,
    @Json(name = "from_who") val fromWho: String,
    @Json(name = "hitokoto") val content: String,
)
