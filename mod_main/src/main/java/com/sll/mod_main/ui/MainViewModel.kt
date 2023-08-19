package com.sll.mod_main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.util.debug
import com.sll.lib_network.ext.request
import com.sll.lib_network.manager.ApiManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/04
 */
class MainViewModel: ViewModel() {
    companion object {
        private val TAG = MainViewModel::class.simpleName

        const val TIPS_NETWORK_ERROR_CLICK = "网络错误，请点击重新加载"
    }

    // 用户信息
    val userInfo = ServiceManager.userService.getUserInfoFlow()

    // 配文
    private val _caption = MutableStateFlow("")
    val caption = _caption.asStateFlow()



    // ================== 网络请求 ===============================

    // 获取配文
    fun getCaption() {
        viewModelScope.launch {
            request {
                 ApiManager.yiyanApi.getSentence("a", 10, 20)
            }.collect { res ->
                res.onSuccess { _caption.value = it }
                res.onError {
                    if (_caption.value.isEmpty()) {
                        _caption.value = TIPS_NETWORK_ERROR_CLICK
                    }
                }
            }
        }
    }


}