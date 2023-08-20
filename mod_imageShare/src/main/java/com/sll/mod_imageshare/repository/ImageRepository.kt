package com.sll.mod_imageshare.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.entity.dto.Paging
import com.sll.lib_network.manager.ApiManager
import com.sll.lib_network.repositroy.BaseRepository
import com.sll.lib_network.response.Response
import com.sll.mod_imageshare.ui.paging.ImagePagingSource
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import java.security.MessageDigest


/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/14
 */
object ImageRepository: BaseRepository() {

//    fun uploadImage() {
//        return ApiManager.api.upload()
//    }
    /**
     * TODO： 从网络中下载下来后，针对不同的图片进行本地缓存，然后获取的图片先是从图片缓存中去找，
     *        如果不存在则重新从网络中获取
     *
     * 1. 发现、关注、点赞这些的图片可以不缓存在磁盘中
     * 2. 草稿、发布的内容、收藏这些图片需要缓存在磁盘中
     * */

//    suspend fun downloadImage(url: String, path: String): ResponseBody {
//
//    }

    /**
     * 获取发现的图文列表
     * */
    fun fetchDiscoverImageShares(): Flow<PagingData<ImageShare>> {
        return Pager(
            config = PagingConfig(pageSize = 15, prefetchDistance = 0),
            pagingSourceFactory = { ImagePagingSource(ApiManager.api) }
        ).flow
    }



}

