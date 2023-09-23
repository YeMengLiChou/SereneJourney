package com.sll.lib_network.api

import com.sll.lib_network.response.ImageResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/23
 */
interface ImageService {

    /***
     * 随机返回一张图片 url
     * */
    @GET("pixiv/")
    suspend fun getRandomImage(
        @Query("type") type: String = "JSON"
    ): ImageResponse
}