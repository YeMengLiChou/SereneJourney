package com.sll.mod_user.reposiotry

import android.net.Uri
import androidx.core.net.toFile
import com.sll.lib_common.entity.Sex
import com.sll.lib_common.entity.dto.ImageSet
import com.sll.lib_network.manager.ApiManager
import com.sll.lib_network.repositroy.BaseRepository
import com.sll.lib_network.response.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/14
 */
object UserRepository: BaseRepository() {

    /**
     * 更新用户信息
     * */
    suspend fun updateUserInfo(
        userId: Long,
        username: String? = null,
        sex: Sex? = null,
        avatar: String? = null,
        introduce: String? = null,
    ): Response<String> {
        // 根据传入的信息创建一个json，以post方法发送请求
        val jsonObject = JSONObject()
        jsonObject.put("id", userId.toString())
        username?.let { jsonObject.put("username", it) }
        sex?.let { jsonObject.put("sex", it.key) }
        avatar?.let { jsonObject.put("avatar", it) }
        introduce?.let { jsonObject.put("introduce", it) }
        val data = jsonObject.toString().toRequestBody("application/json; Accept: application/json".toMediaTypeOrNull())
        return ApiManager.api.updateUserInfo(data)
    }


    /**
     * 更新头像
     * @param uri 上传的图像uri
     * */
    suspend fun uploadAvatar(uri: Uri): Response<ImageSet> {
        val file = uri.toFile()
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("fileList", file.name, requestBody)
        return ApiManager.api.upload(listOf(filePart))
    }
}