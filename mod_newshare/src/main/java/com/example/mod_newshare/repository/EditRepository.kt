package com.example.mod_newshare.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toFile
import com.sll.lib_common.entity.dto.ImageSet
import com.sll.lib_network.api.params.EditParam
import com.sll.lib_network.manager.ApiManager
import com.sll.lib_network.repositroy.BaseRepository
import com.sll.lib_network.response.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object EditRepository : BaseRepository() {


    suspend fun uploadImages(uriList: List<Uri>): Response<ImageSet> {
        val parts = mutableListOf<MultipartBody.Part>()
        for (uri in uriList) {
//            val file = uri.toFile()
            val file = handleImage(uri)
            Log.d("EditRepository",file.toString())
            val requestBody = file!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("fileList", file.name, requestBody)
            parts.add(filePart)
        }
        return ApiManager.api.upload(parts)
    }

    suspend fun newShare(
        title: String,
        content: String,
        imageCode: Long,
        pUserId: Long
    ): Response<String> {
        return ApiManager.api.addShare(EditParam(title, content, 8, imageCode, pUserId))
    }

    suspend fun saveShare(
        title: String,
        content: String,
        imageCode: Long,
        pUserId: Long
    ): Response<String> {
        return ApiManager.api.saveShare(EditParam(title, content, 8, imageCode, pUserId))
    }

    fun handleImage(uri: Uri): File? {
        val contentResolver: ContentResolver = com.therouter.getApplicationContext()!!.contentResolver

        // 使用 ContentResolver 检索图像
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // 将图像保存到文件系统中
        val file = createImageFile()
        val outputStream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.close()

        // 最后，返回保存的文件路径
        return file
    }
    private fun createImageFile(): File {
        val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = com.therouter.getApplicationContext()!!.getExternalFilesDir(
            Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )
    }
}