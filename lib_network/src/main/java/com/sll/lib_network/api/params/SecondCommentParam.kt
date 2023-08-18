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
data class SecondCommentParam(
    val content: String,
    val parentCommentId: Long,
    val parentCommentUserId: Long,
    val replyCommentId: Long,
    val replyCommentUserId: Long,
    val shareId: Long,
    val userId: Long,
    val username: String
)
