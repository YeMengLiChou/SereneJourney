package com.example.mod_newshare.ui

import android.app.AlertDialog
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod_newshare.repository.EditRepository
import com.sll.lib_common.entity.dto.ImageSet
import com.sll.lib_network.ext.requestResponse
import com.sll.lib_network.response.Res
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EditViewModel : ViewModel() {
    companion object{
        const val TAG = "EditViewModel"
    }
    //上传文件
    private val _uploadState = MutableStateFlow<Res<ImageSet?>?>(null)
    val uploadState = _uploadState.asStateFlow()

    //图片Uri列表
    private val _uriList = MutableStateFlow<List<Uri>?>(emptyList())
    val uriList = _uriList.asStateFlow()

    //图片分享
    private val _newShareState = MutableStateFlow<Res<String?>?>(null)
    val newShareState = _newShareState.asStateFlow()

    //保存图文分享
    private val _saveShare = MutableStateFlow<Res<String?>?>(null)
    val saveShare = _saveShare.asStateFlow()
    fun uploadImages(uriList: List<Uri>) {
        viewModelScope.launch {
            requestResponse {
                EditRepository.uploadImages(uriList)
            }.collect { res ->
                res.onSuccess {
                    Log.d("uploadImageSuccessful", it?.imageCode.toString())
                    _uploadState.value = res
                }
            }
        }
    }

    fun newShare(
        title: String,
        content: String,
        imageCode: Long,
        pUserId: Long
    ) {
        viewModelScope.launch {
            requestResponse {
                EditRepository.newShare(title, content, imageCode, pUserId)
            }.collect { res ->
                _newShareState.value = res
                res.onSuccess {
                    Log.d("newShareSuccessful", res.toString())
                }
            }
        }
    }

    fun addUri(uri: Uri) {
        val list = _uriList.value?.toMutableList()
        list?.add(uri)
        _uriList.value = list
    }

    fun removeAllUri() {
        val list = _uriList.value?.toMutableList()
        list?.clear()
        _uriList.value = list
    }

    fun removeUri(index: Int){
        val list = _uriList.value?.toMutableList()
        list?.removeAt(index)
        _uriList.value = list
    }

    fun saveShare(
        title: String,
        content: String,
        imageCode: Long,
        pUserId: Long
    ) {
        viewModelScope.launch {
            requestResponse {
                EditRepository.saveShare(title,content,imageCode,pUserId)
            }.collect {res ->
                _saveShare.value = res
                res.onSuccess {
                    Log.d(TAG, "saveDraft: successful")
                }
            }
        }
    }


}