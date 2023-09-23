package com.sll.lib_network.api

import com.sll.lib_common.entity.dto.Comment
import com.sll.lib_common.entity.dto.ImageSet
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.entity.dto.Paging
import com.sll.lib_common.entity.dto.User
import com.sll.lib_network.api.params.CommentParam
import com.sll.lib_network.api.params.EditParam
import com.sll.lib_network.api.params.SecondCommentParam
import com.sll.lib_network.api.params.ShareParam
import com.sll.lib_network.api.params.UserParam
import com.sll.lib_network.response.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */
interface ApiService {

    /**
     * 获取当前登录用户收藏图文列表 [文档](http://47.107.52.7/api/detail/info?detailId=1&appId=857)
     * @param current 当前页
     * @param size 页面大小
     * @param userId 当前登录用户的 Id
     */
    @GET("collect")
    suspend fun listCollectedShare(
        @Query("current") current: Int = 0,
        @Query("size") size: Int = 10,
        @Query("userId") userId: Long
    ): Response<Paging<ImageShare>>


    /**
     * 图文分享的主键id [文档](http://47.107.52.7/api/detail/info?detailId=2&appId=857)
     * @param shareId 图文分享的主键id，通过图文分享发现列表获取id
     * @param userId 当前登录用户的 Id
     * */
    @POST("collect")
    suspend fun collectShare(
        @Query("shareId") shareId: String,
        @Query("userId") userId: Long
    ): Response<String>


    /**
     * 用户取消对图文分享的收藏 [文档](http://47.107.52.7/api/detail/info?detailId=3&appId=857)
     * @param shareId 图文分享的主键id，通过图文分享发现列表获取id
     * @param userId 当前登录用户的 Id
     * */
    @POST("collect/cancel")
    suspend fun cancelCollectShare(
        @Query("collectId") collectId: String,
    ): Response<String>


    /**
     * 获取一级评论 [文档](http://47.107.52.7/api/detail/info?detailId=4&appId=857)
     * @param current 当前页
     * @param size 页面大小
     * @param shareId 当前登录用户的 Id
     */
    @GET("comment/first")
    suspend fun listFirstComment(
        @Query("current") current: Int = 0,
        @Query("size") size: Int = 10,
        @Query("shareId") shareId: Long
    ): Response<Paging<Comment>>


    /**
     * 新增一个图片分享的一级评论 [文档](http://47.107.52.7/api/detail/info?detailId=5&appId=857)
     * @param content 评论的内容【新增成功后可到 [listFirstComment] 查询】
     * @param shareId 图文分享的主键id
     * @param userId 评论人userId
     * @param username 评论人用户名
     */
    @POST("comment/first")
    @Headers("Content-Type: application/json")
    suspend fun addFirstComment(
        @Body param: CommentParam
    ): Response<String>


    /**
     * 获取二级评论 [文档](http://47.107.52.7/api/detail/info?detailId=6&appId=857)
     * @param commentId 一级评论id【可通过 [listFirstComment] 获取id】
     * @param current 当前页
     * @param size 页面大小
     * @param shareId 当前登录用户的 Id
     */
    @GET("comment/second")
    suspend fun listSecondComment(
        @Query("commentId") commentId: Long,
        @Query("current") current: Int = 0,
        @Query("size") size: Int = 10,
        @Query("shareId") shareId: Long
    ): Response<Paging<Comment>>


    /**
     * 新增一个图片分享的二级评论或回复 [文档](http://47.107.52.7/api/detail/info?detailId=7&appId=857)
     * @param content 评论的内容【新增成功后可到 [listSecondComment] 查询】
     * @param parentCommentId 一级评论id【可通过 [listFirstComment] 获取id】
     * @param parentCommentUserId 一级评论的用户id【可通过 [listFirstComment] 获取id】
     * @param replyCommentId 被回复的评论id
     * @param replyCommentUserId 被回复的评论用户id
     * @param shareId 图文分享的主键id
     * @param userId 评论人userId
     * @param username 评论人用户名
     */
    @POST("comment/second")
    @Headers("Content-Type: application/json")
    suspend fun addSecondComment(
        @Body param: SecondCommentParam
    ): Response<String>
//    @POST("comment/second")
//    @Headers("Content-Type: application/json")
//    suspend fun addSecondComment(
//         @Query("content") content: String,
//         @Query("parentCommentId") parentCommentId: Long,
//         @Query("parentCommentUserId") parentCommentUserId: Long,
//         @Query("replyCommentId") replyCommentId: Long,
//         @Query("replyCommentUserId") replyCommentUserId: Long,
//         @Query("shareId") shareId: Long,
//         @Query("userId") userId: Long,
//         @Query("username") username: String
//    ): Response<String>


    /**
     * 获取当前登录用户已关注的图文列表 [文档](http://47.107.52.7/api/detail/info?detailId=8&appId=857)
     * @param current 当前页
     * @param size 页面大小
     * @param userId 当前登录用户的 Id
     * */
    @GET("focus")
    suspend fun listFocusShare(
        @Query("current") current: Int = 0,
        @Query("size") size: Int = 10,
        @Query("userId") userId: Long
    ): Response<Paging<ImageShare>>


    /**
     * 添加关注 [文档](http://47.107.52.7/api/detail/info?detailId=9&appId=857)
     * @param focusUserId 被关注的用户id
     * @param userId 当前登录的用户id
     *
     * */
    @POST("focus")
    suspend fun focusUser(
        @Query("focusUserId") focusUserId: String,
        @Query("userId") userId: Long
    ): Response<String>


    /**
     * 取消关注 [文档](http://47.107.52.7/api/detail/info?detailId=10&appId=857)
     * @param focusUserId 被关注的用户id 【如果未关注 先关注 [focusUser]】
     * @param userId 当前登录用户id
     */
    @POST("focus/cancel")
    suspend fun cancelFocus(
        @Query("focusUserId") focusUserId: String,
        @Query("userId") userId: Long
    ): Response<String>


    /**
     * 上传文件 [文档](http://47.107.52.7/api/detail/info?detailId=11&appId=857)
     * @param files
     */
    @Multipart
    @POST("image/upload")
    suspend fun upload(@Part files: List<MultipartBody.Part>): Response<ImageSet>


    /**
     * 获取当前登录用户点赞图文列表 [文档](http://47.107.52.7/api/detail/info?detailId=12&appId=857)
     * @param current 当前页
     * @param size 页面大小
     * @param userId 当前登录用户的 Id
     */
    @GET("like")
    suspend fun listLikedShare(
        @Query("current") current: Int = 0,
        @Query("size") size: Int = 10,
        @Query("userId") userId: Long
    ): Response<Paging<ImageShare>>


    /**
     * 用户对图文分享进行点赞 [文档](http://47.107.52.7/api/detail/info?detailId=13&appId=857)
     * @param shareId 图文分享的id
     * @param userId 当前登录用户的id
     */
    @POST("like")
    @Headers("Content-Type: application/json")
    suspend fun likeShare(
        @Query("shareId") shareId: String,
        @Query("userId") userId: Long,
    ): Response<String>


    /**
     * 用户取消对图文分享的点赞 [文档](http://47.107.52.7/api/detail/info?detailId=14&appId=857)
     * @param likeId 图文分享的id
     */
    @POST("like/cancel")
    @Headers("Content-Type: application/json")
    suspend fun cancelLike(
        @Query("likeId") likeId: String
    ): Response<String>


    /**
     * 获取图片分享发现列表 [文档](http://47.107.52.7/api/detail/info?detailId=15&appId=857)
     * @param current 当前页
     * @param size 页面大小
     * @param userId 当前登录用户的 Id
     */
    @GET("share")
    suspend fun listDiscoverShare(
        @Query("current") current: Int = 0,
        @Query("size") size: Int = 10,
        @Query("userId") userId: Long
    ): Response<Paging<ImageShare>>


    /**
     * 新增图文分享 [文档](http://47.107.52.7/api/detail/info?detailId=16&appId=857)
     * @param title 标题
     * @param content 内容
     * @param id 主键id
     * @param imageCode 一组图片的唯一标识符，通过 [upload] 获取
     * @param pUserId 当前登录用户id
     * */
    @POST("share/add")
    @Headers("Content-Type: application/json")
    suspend fun addShare(
        @Body param: EditParam
    ): Response<String>


    /**
     * 将保存状态修改为发布状态 [文档](http://47.107.52.7/api/detail/info?detailId=17&appId=857)
     * @param title 标题
     * @param content 内容
     * @param id 主键id
     * @param imageCode 一组图片的唯一标识符，通过 [upload] 获取
     * @param pUserId 当前登录用户id
     */
    @POST("share/change")
    @Headers("Content-Type: application/json")
    suspend fun publishShare(
        @Body param: EditParam
    ): Response<String>


    /**
     * 删除图文分享 [文档](http://47.107.52.7/api/detail/info?detailId=18&appId=857)
     * @param shareId 当前要删除的图文id
     * @param userId 当前登录用户id
     */
    @POST("share/delete")
    suspend fun deleteShare(
        @Body param: ShareParam
    ): Response<String>


    /**
     * 获取单个图文分享的详情 [文档](http://47.107.52.7/api/detail/info?detailId=19&appId=857)
     * @param shareId 当前要查看的图文id
     * @param userId 当前登录用户id
     */
    @GET("share/detail")
    suspend fun getShareDetail(
        @Query("shareId") shareId: Long,
        @Query("userId") userId: Long
    ): Response<ImageShare>


    /**
     * 获取我的动态图片分享列表 [文档](http://47.107.52.7/api/detail/info?detailId=20&appId=857)
     * @param current 当前页
     * @param size 页面大小
     * @param userId 当前登录用户的 Id
     */
    @GET("share/myself")
    suspend fun listMyselfShare(
        @Query("current") current: Int = 0,
        @Query("size") size: Int = 10,
        @Query("userId") userId: Long
    ): Response<Paging<ImageShare>>


    /**
     * 获取已保存的图文分享列表 [文档](http://47.107.52.7/api/detail/info?detailId=21&appId=857)
     * @param current 当前页【如果没有保存，先保存[]】
     * @param size 页面大小
     * @param userId 当前登录用户的 Id
     */
    @GET("share/save")
    suspend fun listSavedShare(
        @Query("current") current: Int = 0,
        @Query("size") size: Int = 10,
        @Query("userId") userId: Long
    ): Response<Paging<ImageShare>>


    /**
     * 保存图文分享 [文档](http://47.107.52.7/api/detail/info?detailId=22&appId=857)
     * @param title 标题
     * @param content 内容
     * @param id 主键id
     * @param imageCode 一组图片的唯一标识符，通过 [upload] 获取
     * @param pUserId 当前登录用户id
     */
    @POST("share/save")
    @Headers("Content-Type: application/json")
    suspend fun saveShare(
        @Body param: EditParam
    ): Response<String>


    /**
     * 用户登录 [文档](http://47.107.52.7/api/detail/info?detailId=23&appId=857)
     *  @param param 参数
     */
    @POST("user/login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String,
    ): Response<User>


    /**
     * 创建用户 [文档](http://47.107.52.7/api/detail/info?detailId=24&appId=857)
     *  @param username 用户名
     *  @param password 密码
     */
    @POST("user/register")
    @Headers("Content-Type: application/json")
    suspend fun register(
        @Body param: UserParam
    ): Response<String>


    /**
     * 修改用户信息 [文档](http://47.107.52.7/api/detail/info?detailId=24&appId=857)
     *
     * 如果不是全部修改，则请用 [updateUserInfo(params)]
     * @param avatarUrl 头像url， 需要先 [upload] 文件
     * @param userId 用户主键id
     * @param introduce 个人介绍
     * @param sex 性别
     * @param username 用户名
     * */
    @POST("user/update")
    suspend fun updateUserInfo(
        @Body param: RequestBody
    ): Response<String>

    @GET("user/getUserByName")
    suspend fun getUserByName(
        @Query("username") username: String
    ): Response<User>


}

