package com.sll.lib_network.response

import com.sll.lib_network.constant.CODE_OK
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 网络请求响应
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */
@JsonClass(generateAdapter = true)
data class Response <out T> (
    val code: Int,
    @Json(name = "msg")
    val message: String? = "",
    val data: T?
) {
    fun isSuccess(): Boolean = code == CODE_OK

    fun isError(): Boolean = code != CODE_OK
}
