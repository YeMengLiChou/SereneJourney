package com.sll.lib_network.api

import androidx.annotation.IntRange
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 一言的调用接口 [文档](https://developer.hitokoto.cn/sentence/)
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/10
 */
interface YiYanService {
    @GET("/")
    suspend fun getSentence(
        @Query("c") type: String,
        @Query("min_length") @IntRange(from = 0) minLength: Int = 5,
        @Query("max_length") @IntRange(from = 15) maxLength: Int = 15,
        @Query("encode") encode: String = "text"
    ): String

}