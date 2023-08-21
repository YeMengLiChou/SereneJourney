package com.sll.mod_imageshare.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_network.manager.ApiManager
import com.sll.lib_network.repositroy.BaseRepository
import com.sll.lib_network.response.Res
import com.sll.mod_imageshare.ui.paging.CollectPagingSource
import com.sll.mod_imageshare.ui.paging.FocusPagingSource
import com.sll.mod_imageshare.ui.paging.DiscoverPagingSource
import com.sll.mod_imageshare.ui.paging.LikePagingSource
import kotlinx.coroutines.flow.Flow


/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/14
 */
object ImageRepository: BaseRepository() {

    /**
     * TODO： 从网络中下载下来后，针对不同的图片进行本地缓存，然后获取的图片先是从图片缓存中去找，
     *        如果不存在则重新从网络中获取
     *
     * 1. 发现、关注、点赞这些的图片可以不缓存在磁盘中
     * 2. 草稿、发布的内容、收藏这些图片需要缓存在磁盘中
     * */


    private val userId get() = ServiceManager.userService.getUserInfo()?.id ?: 0

    /**
     * 获取发现的图文列表
     * */
    fun fetchDiscoverImageShares(): Flow<PagingData<ImageShare>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15
            ),
            pagingSourceFactory = { DiscoverPagingSource(ApiManager.api) }
        ).flow
    }


    fun fetchFocusImageShare(): Flow<PagingData<ImageShare>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15
            ),
            pagingSourceFactory = { FocusPagingSource(ApiManager.api) }
        ).flow
    }

    fun fetchLikeImageShare(): Flow<PagingData<ImageShare>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15
            ),
            pagingSourceFactory = { LikePagingSource(ApiManager.api) }
        ).flow
    }

    fun fetchCollectImageShare(): Flow<PagingData<ImageShare>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15
            ),
            pagingSourceFactory = { CollectPagingSource(ApiManager.api) }
        ).flow
    }

    /**
     * 更新图文信息
     *
     * */
    suspend fun updateImageShareDetail(updateImageShare: ImageShare) {
        requestResponse {
            ApiManager.api.getShareDetail(updateImageShare.id.toLong(), userId)
        }.collect { res ->
            res.onSuccess { updateImageShare.update(it!!) }
        }
    }

    /**
     * 取消点赞
     * @param likeId 点赞的id
     * */
    fun cancelLikeImageShare(likeId: String): Flow<Res<String?>> {
        return requestResponse {
            ApiManager.api.cancelLike(likeId)
        }
    }
    /**
     * 点赞
     * @param shareId 图文的id
     * */
    fun likeImageShare(shareId: String): Flow<Res<String?>> {
        return requestResponse {
            ApiManager.api.likeShare(shareId, userId)
        }
    }

    /**
     * 取消收藏
     * */
    fun cancelCollectImageShare(shareId: String): Flow<Res<String?>> {
        return requestResponse {
            ApiManager.api.cancelCollectShare(shareId, userId)
        }
    }

    /**
     * 收藏
     * */
    fun collectImageShare(shareId: String): Flow<Res<String?>> {
        return requestResponse {
            ApiManager.api.collectShare(shareId, userId)
        }
    }


    /**
     * 关注
     * */
    fun focus(focusUserId: String): Flow<Res<String?>> {
        return requestResponse {
            ApiManager.api.focusUser(focusUserId, userId)
        }
    }
    /**
     * 取消关注
     * */
    fun cancelFocus(focusUserId: String): Flow<Res<String?>> {
        return requestResponse {
            ApiManager.api.cancelFocus(focusUserId, userId)
        }
    }
}

