package com.sll.lib_network.api.params

import com.squareup.moshi.JsonClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/12
 */
@JsonClass(generateAdapter = true)
data class CommentParam(
    val shareId: Long,
    val userId: Long,
    val username: String,
    val content: String
)
