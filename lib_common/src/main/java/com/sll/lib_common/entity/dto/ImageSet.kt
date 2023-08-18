package com.sll.lib_common.entity.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */

@JsonClass(generateAdapter = true)
data class ImageSet(
    // 一组图片的唯一标识符
    val imageCode: Long,
    // 图片的网络资源链接
    @Json(name = "imageUrlList") val urls: List<String>
)
