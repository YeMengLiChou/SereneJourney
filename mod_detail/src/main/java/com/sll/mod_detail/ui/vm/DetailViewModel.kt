package com.sll.mod_detail.ui.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sll.lib_common.entity.dto.Comment
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_network.ext.requestResponse
import com.sll.lib_network.response.Res
import com.sll.mod_detail.repository.DetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/12
 */
class DetailViewModel : ViewModel() {
    companion object {
        private const val TAG = "DetailViewModel"
    }

    // ===================== ImageShare ===========================

    // 当前Activity展示的
    private val _imageShare = MutableStateFlow<ImageShare?>(null)
    val imageShare = _imageShare.asStateFlow()

    // 图文分享的是否点赞的状态
    private val _imageShareLikeState = MutableStateFlow<Res<String?>>(Res.Loading)
    val imageShareLikeState = _imageShareLikeState.asStateFlow()

    // 图文分享是否收藏的状态
    private val _imageShareCollectState = MutableStateFlow<Res<String?>>(Res.Loading)
    val imageShareCollectState = _imageShareCollectState.asStateFlow()

    private val currentShareId get() =  imageShare.value!!.id


    // 更新图文分享信息
    private fun updateImageShare(shareId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            requestResponse {
                DetailRepository.getImageShareDetail(shareId)
            }.collect { res ->
                // 更新成功后赋值
                res.onSuccess { success ->
                    success?.let { _imageShare.value?.update(it) }
                }
            }
        }
    }

    /**
     * 设置传递过来的图文分享，同时进行更新
     * */
    fun setImageShare(imageShare: ImageShare) {
        _imageShare.value = imageShare
        updateImageShare(imageShare.id)
    }

    // 点赞
    fun likeOrCancelImageShare() {
        val preLike =
        viewModelScope.launch(Dispatchers.IO) {
            requestResponse {
                DetailRepository.likeImageShare(currentShareId.toLong())
            }.collect { res ->
                res.onSuccess {
                    // 关注成功
                    _userFocusState.value = true
                    // 需要获取likeId
                    updateImageShare(imageShare.value!!.id)
                }
            }
        }
    }

    fun cancelLikeImageShare() {
        viewModelScope.launch(Dispatchers.IO) {
            requestResponse {
                DetailRepository.cancelFocusUser(imageShare.value!!.likeId.toString())
            }.collect { res ->
                res.onSuccess {
                    // 取消关注
                    _userFocusState.value = false
                    _imageShare.value!!.likeId = null
                }
            }
        }
    }

    fun collectImageShare() {
        viewModelScope.launch(Dispatchers.IO) {
            requestResponse {
                DetailRepository.collectImageShare(currentShareId.toLong())
            }.collect { res ->
                res.onSuccess {
                    // 关注成功
                    _userFocusState.value = true
                }
            }
        }
    }

    fun cancelCollectImageShare() {
        viewModelScope.launch(Dispatchers.IO) {
            requestResponse {
                DetailRepository.cancelFocusUser(currentShareId)
            }.collect { res ->
                res.onSuccess {
                    // 取消关注
                    _userFocusState.value = false
                }
            }
        }
    }


    // ====================== UserInfo =============================

    // 查询到的用户信息
    private val _userInfoState = MutableStateFlow<Res<User?>?>(null)
    val userInfoState = _userInfoState.asStateFlow()

    // 用户关注的状态
    private val _userFocusState = MutableStateFlow(false)
    val userFocusState = _userFocusState.asStateFlow()

    // 用户是否已经关注
    var userFocus
        get() = _userFocusState.value
        set(value) {
            _userFocusState.value = value
        }

    /**
     * 根据用户名获取用户信息
     * */
    fun getUserInfoByUsername(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            requestResponse {
                DetailRepository.getUserByName(username)
            }.collect { res ->
                _userInfoState.value = res
            }
        }
    }

    // 关注或取消关注用户
    fun focusOrCancelUser() {
        val pUserId = imageShare.value!!.pUserId
        val preFocus = userFocus
        viewModelScope.launch(Dispatchers.IO) {
            requestResponse {
                Log.i(TAG, "focusOrCancelUser: preFocus: $preFocus")
                if (preFocus) {
                    DetailRepository.cancelFocusUser(pUserId)
                } else {
                    DetailRepository.focusUser(pUserId)
                }
            }.collect { res ->
                res.onSuccess {
                    // 关注成功
                    _userFocusState.value = !preFocus
                }
            }
        }
    }


    // ====================== Comments ==============================

    private val _addFirstLevelCommentState = MutableStateFlow<Res<String?>?>(null)
    val addFirstLevelCommentState = _addFirstLevelCommentState.asStateFlow()

    /** 获取一级评论并缓存在ViewModel中  */
    fun getFirstLevelComments() = DetailRepository.getFirstLevelCommentsPagingSource(imageShare.value!!.id.toLong()).cachedIn(viewModelScope)

    /** 添加一级评论时的内容字符串 */
    var mAddFirstLevelCommentContent = ""


    fun addFirstLevelComment() {
        viewModelScope.launch(Dispatchers.IO) {
            requestResponse {
                // 发送请求
                DetailRepository.addFirstLevelComment(
                    mAddFirstLevelCommentContent,
                    imageShare.value!!.id.toLong()
                )
            }.collect { res ->
                _addFirstLevelCommentState.value = res
            }
        }
    }

    fun getSecondLevelComments(commentId: Long) =
        DetailRepository.getSecondLevelCommentPagingDataFlow(currentShareId.toLong(), commentId).cachedIn(viewModelScope)


    /**
     * 评论详情中所需要的一级评论
     * */
    var detailParentComment: Comment? = null

    var detailCommentCount: Int = 0

    /**
     * 当从评论详情中返回时是否需要刷新
     * */
    private val _detailNeedRefresh = MutableStateFlow(false)
    val detailNeedRefresh = _detailNeedRefresh.asStateFlow()


    fun setNeedRefresh(refresh: Boolean) {
        this._detailNeedRefresh.value = refresh
    }

}

