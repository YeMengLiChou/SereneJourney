package com.sll.lib_common.entity.dto

import com.squareup.moshi.JsonClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
@JsonClass(generateAdapter = true)
data class LoginInfo(
    val username: String,
    val password: String,
    val remember: Boolean,
    val timeStamp: Long
)
