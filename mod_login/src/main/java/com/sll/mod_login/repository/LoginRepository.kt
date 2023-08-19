package com.sll.mod_login.repository

import com.sll.lib_common.entity.dto.User
import com.sll.lib_network.api.params.UserParam
import com.sll.lib_network.manager.ApiManager
import com.sll.lib_network.repositroy.BaseRepository
import com.sll.lib_network.response.Response

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/14
 */
object LoginRepository: BaseRepository() {

    /**
     * 登录
     * @param username
     * @param password
     * */
    suspend fun login(username: String, password: String): Response<User> {
        return ApiManager.api.login(username, password)
    }

    /**
     * @param username
     * @param 注册
     * */
    suspend fun register(username: String, password: String): Response<String> {
        return ApiManager.api.register(UserParam(username, password))
    }

}