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
data class FocusParam(
    val focusUserId: Long,
    val userId: Long
)
