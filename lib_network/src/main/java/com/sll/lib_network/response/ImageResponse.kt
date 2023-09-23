package com.sll.lib_network.response

import com.squareup.moshi.JsonClass

/**
 * 用于 ImageService Api 返回的
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/23
 */
@JsonClass(generateAdapter = true)
data class ImageResponse(
    val code: Int,
    val url: String,
    val width: Int,
    val height: Int,
    val size: String,
    val mime: String
)