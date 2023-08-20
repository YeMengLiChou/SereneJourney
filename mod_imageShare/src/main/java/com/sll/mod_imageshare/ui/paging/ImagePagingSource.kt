package com.sll.mod_imageshare.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.util.debug
import com.sll.lib_network.api.ApiService
import com.sll.lib_network.error.ApiException
import com.sll.lib_network.error.ERROR
import com.sll.lib_network.error.ExceptionHandler

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
class ImagePagingSource(
    private val apiService: ApiService
): PagingSource<Int, ImageShare>() {
    companion object {
        private const val TAG = "ImagePagingSource"
    }

    override fun getRefreshKey(state: PagingState<Int, ImageShare>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageShare> {
        return try {
            // 当前页号
            val page = params.key ?: 1
            // 请求内容
            val response = apiService.listDiscoverShare(page, 15, ServiceManager.userService.getUserInfo()!!.id)
            // 分页内容
            val data = response.data?.records
            return if (data != null) {
                LoadResult.Page(
                    data = data,
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if (data.isNotEmpty()) page + 1 else null
                )
            } else {
                LoadResult.Error(ApiException(ERROR.NETWORK_ERROR))
            }
        } catch (e : Exception) {
            LoadResult.Error(ExceptionHandler.handle(e))
        }
    }
}