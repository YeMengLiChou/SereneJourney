package com.sll.mod_detail.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sll.lib_common.entity.dto.Comment
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.entity.dto.Paging
import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_network.api.params.CommentParam
import com.sll.lib_network.api.params.SecondCommentParam
import com.sll.lib_network.manager.ApiManager
import com.sll.lib_network.repositroy.BaseRepository
import com.sll.lib_network.response.Response
import com.sll.mod_detail.adapter.pagingSource.FirstCommentPagingSource
import com.sll.mod_detail.adapter.pagingSource.SecondCommentPagingSource
import kotlinx.coroutines.flow.Flow
import java.util.StringTokenizer

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/17
 */
object DetailRepository: BaseRepository() {

    private val currentUserId get() = ServiceManager.userService.getUserInfo()?.id

    private val currentUsername get() = ServiceManager.userService.getUserInfo()?.username

    /**
     * 通过用户名获取用户信息
     * */
    suspend fun getUserByName(
        username: String
    ): Response<User> {
        return ApiManager.api.getUserByName(username)
    }

    /**
     * 获取一级评论
     * @param current
     * @param shareId
     * @param size
     * */
    suspend fun getFirstLevelComments(
        current: Int,
        shareId: Long,
        size: Int
    ): Response<Paging<Comment>> {
        return ApiManager.api.listFirstComment(current, size, shareId)
    }

    /**
     * 获取一级评论的 [PagingData]
     * */
    fun getFirstLevelCommentsPagingSource(
        shareId: Long,
    ): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15
            ),
            pagingSourceFactory = { FirstCommentPagingSource(shareId) }
        ).flow
    }

    /**
     * 添加一个一级评论
     * */
    suspend fun addFirstLevelComment(
        content: String,
        shareId: Long,
    ): Response<String> {
        return ApiManager.api.addFirstComment(CommentParam(shareId, userId = currentUserId!!, username = currentUsername!!, content))
    }


    /**
     * 获取二级评论
     * @param commentId
     * @param current
     * @param size
     * @param shareId
     * */
    suspend fun getSecondLevelComments(
        commentId: Long,
        current: Int,
        size: Int,
        shareId: Long
    ): Response<Paging<Comment>> {
        return ApiManager.api.listSecondComment(commentId, current, size, shareId)
    }

    fun getSecondLevelCommentPagingDataFlow(
        shareId: Long,
        commentId: Long
    ): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15
            ),
            pagingSourceFactory = { SecondCommentPagingSource(shareId, commentId) }
        ).flow
    }


    suspend fun addSecondLevelComment(
        content: String,
        parentCommentId: Long,
        parentUserId: Long,
        replyCommentId: Long,
        replyUserId: Long,
        shareId: Long,
    ): Response<String> {
        return ApiManager.api.addSecondComment(SecondCommentParam(
            content,
            parentCommentId,
            parentUserId,
            replyCommentId,
            replyUserId,
            shareId,
            currentUserId!!,
            currentUsername!!
        ))
    }


    /**
     * 获取图文分享的详情
     * */
    suspend fun getImageShareDetail(shareId: String): Response<ImageShare> {
        return ApiManager.api.getShareDetail(shareId.toLong(), userId = currentUserId!!)
    }

    /**
     * 关注用户
     * */
    suspend fun focusUser(focusUserId: String): Response<String> {
        return ApiManager.api.focusUser(focusUserId, userId = currentUserId!!)
    }

    /**
     * 取消关注用户
     * */
    suspend fun cancelFocusUser(focusUserId: String): Response<String> {
        return ApiManager.api.cancelFocus(focusUserId, userId = currentUserId!!)
    }

    /**
     * 给图文分享点赞
     * */
    suspend fun likeImageShare(shareId: Long): Response<String> {
        return ApiManager.api.likeShare(shareId.toString(), userId = currentUserId!!)
    }

    /**
     * 取消给图文分享点赞
     * */
    suspend fun cancelLikeImageShare(likeId: Long): Response<String> {
        return ApiManager.api.cancelLike(likeId.toString())
    }

    /**
     * 收藏图文分享
     * */
    suspend fun collectImageShare(shareId: Long): Response<String> {
        return ApiManager.api.collectShare(shareId.toString(), userId = currentUserId!!)
    }

    /**
     * 取消收藏图文分享
     * */
    suspend fun cancelCollectImageShare(shareId: Long): Response<String> {
        return ApiManager.api.cancelCollectShare(shareId.toString(), userId = currentUserId!!)
    }

}