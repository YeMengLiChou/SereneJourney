package com.sll.lib_network.api.params

import com.squareup.moshi.JsonClass
import retrofit2.http.Query

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/12
 */
@JsonClass(generateAdapter = true)
data class UserInfoParam(
    val avatarUrl: String,
    val userId: Long,
    val introduce: String,
    val sex: Int,
    val username: String
)
