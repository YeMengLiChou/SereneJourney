package com.sll.mod_detail.adapter.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sll.lib_common.entity.dto.Comment
import com.sll.lib_network.error.ApiException
import com.sll.lib_network.error.ERROR
import com.sll.lib_network.error.ExceptionHandler
import com.sll.mod_detail.repository.DetailRepository

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/19
 */
class FirstCommentPagingSource(
    private val shareId: Long
): PagingSource<Int, Comment>() {
    companion object {
        private const val TAG = "FirstCommentPaging"
    }

    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        return try {
            // 当前页号
            val page = params.key ?: 1
            // 请求内容
            val response = DetailRepository.getFirstLevelComments(page, shareId, 15)
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