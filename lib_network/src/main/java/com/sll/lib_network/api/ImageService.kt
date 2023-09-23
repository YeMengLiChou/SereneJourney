package com.sll.lib_network.api

import retrofit2.http.GET

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/23
 */
interface ImageService {

    @GET
    fun getRandomImage()
}