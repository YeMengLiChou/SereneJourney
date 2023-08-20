package com.sll.mod_imageshare.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sll.lib_common.entity.dto.ImageSet
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.util.debug
import com.sll.lib_network.api.ApiService
import com.sll.lib_network.error.ApiException
import com.sll.lib_network.error.ERROR
import com.sll.lib_network.error.ExceptionHandler
import com.sll.lib_network.response.Res
import com.sll.mod_imageshare.repository.ImageRepository

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
//        return state.anchorPosition?.let {
//            val anchorPage = state.closestPageToPosition(it)
//            anchorPage?.prevKey?.plus(1)
//                ?: anchorPage?.nextKey?.minus(1)
//        }
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageShare> {
        return try {
            // 下一页页号
            val nextPagerNumber = params.key ?: 0
            // 请求内容
            val response = apiService.listDiscoverShare(nextPagerNumber, 15, ServiceManager.userService.getUserInfo()!!.id)

            debug("paging", "key ${nextPagerNumber}, cur: ${response.data?.current}")
            // 分页内容
            val data = response.data?.records
            return if (data != null) {
                LoadResult.Page(
                    data = data,
                    prevKey = if (nextPagerNumber > 0) nextPagerNumber - 1 else null,
                    nextKey = if (data.isNotEmpty()) nextPagerNumber + 1 else null
                )
            } else {
                LoadResult.Error(ApiException(ERROR.NETWORK_ERROR))
            }
        } catch (e : Exception) {
            LoadResult.Error(ExceptionHandler.handle(e))
        }

    }
}