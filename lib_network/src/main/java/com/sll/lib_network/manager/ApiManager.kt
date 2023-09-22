package com.sll.lib_network.manager

import com.sll.lib_framework.ext.lazyNone
import com.sll.lib_framework.ext.moshi
import com.sll.lib_network.api.ApiService
import com.sll.lib_network.api.YiYanService
import com.sll.lib_network.constant.APP_ID
import com.sll.lib_network.constant.APP_SECRET
import com.sll.lib_network.constant.BASE_URL_MAIN
import com.sll.lib_network.constant.BASE_URL_YIYAN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */
object ApiManager {
    // 图文分享 api
    val api by lazy {
        RetrofitManager.create(
            RetrofitManager.buildInstance(BASE_URL_MAIN) {
                this.addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                    .client(
                        RetrofitManager.initOkHttpClient(
                            Headers.headersOf(
                                "appId", APP_ID,
                                "appSecret", APP_SECRET,
                                "Accept", "application/json, text/plain, */*",
                                "Content-Type", "application/json"
                            )
                        )
                    )
            },
            ApiService::class.java
        )
    }

    // 一言 api
    val yiyanApi by lazy {
        RetrofitManager.create(
            RetrofitManager.buildInstance(BASE_URL_YIYAN) {
                this.addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                client(RetrofitManager.initOkHttpClient())
            },
            YiYanService::class.java
        )
    }

}
