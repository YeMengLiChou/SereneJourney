package com.sll.lib_network.response

import com.sll.lib_network.response.DownloadState.Downloading
import com.sll.lib_network.response.DownloadState.Failed
import com.sll.lib_network.response.DownloadState.Finished
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.io.File

/**
 * 下载状态，分为
 * - **下载中**[Downloading]
 * - **下载结束**[Finished]
 * - **下载失败**[Failed]
 *
 *
 * @author Gleamrise
 * <br/> Created: 2023/09/25
 */
sealed class DownloadState {
    /**
     * 下载状态
     * @param progress 进度条 [0, 100]
     * @param curBytes 当前字节数
     * @param totalBytes 总字节数
     * */
    data class Downloading(
        val progress: Int,
        val curBytes: Long,
        val totalBytes: Long
    ) : DownloadState()

    object Finished : DownloadState()

    data class Failed(val error: Throwable? = null) : DownloadState()
}


/**
 * 保存到目标文件
 * */
fun ResponseBody.saveToFile(destFile: File): Flow<DownloadState> {
    return flow {
        try {
            byteStream().use { input ->
                destFile.outputStream().use { out ->
                    // 总字节数
                    val totalBytes = contentLength()

                    emit(Downloading(0, 0, totalBytes))
                    // 缓冲区
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    // 当前字节数
                    var progressBytes = 0L
                    // 读取的字节数，当为-1时表示读取完毕
                    var bytes = input.read(buffer)
                    // 循环读取
                    while (bytes >= 0) {
                        out.write(buffer, 0, bytes)
                        progressBytes += bytes
                        bytes = input.read(buffer)
                        emit(
                            Downloading(
                                (progressBytes * 100 / totalBytes).toInt(),
                                progressBytes,
                                totalBytes
                            )
                        )
                    }
                }
            }
            // 下载完毕
            emit(DownloadState.Finished)
        } catch (e: Exception) {
            emit(DownloadState.Failed(e))
        }
    }.flowOn(Dispatchers.IO).distinctUntilChanged()
}
