package com.sll.lib_network.repositroy

import android.util.Log
import com.sll.lib_network.error.ApiException
import com.sll.lib_network.error.ERROR
import com.sll.lib_network.error.ExceptionHandler
import com.sll.lib_network.response.Res
import com.sll.lib_network.response.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */
abstract class BaseRepository {

    /**
     * 网络请求，已经指定在 IO 线程中执行
     * @param timeout 设置超时(ms)，默认为不超时, <= 0 不超时, > 0 指定超时时间
     * @param retry 重试次数，默认为不重试 0
     * @param request 请求调用
     * @return flow RequestResult
     * */
    fun <T> requestResponse(
        timeout: Long = -1L,
        retry: Int = 0,
        request: suspend () -> Response<T>,
    ): Flow<Res<T?>> {
        var loading = false

        return flow {
            // 请求发送中可以加载弹窗
            if (!loading) { // 保证重试时仅发送一次 Loading
                loading = true
                emit(Res.Loading)
            }

            val response: Response<T>? = if (timeout > 0) {
                withTimeoutOrNull(timeout) {
                    request()
                }
            } else {
                request()
            }
            // 超时
            if (response == null) throw ApiException(ERROR.TIMEOUT_ERROR)

            // 请求失败
            if (response.isError()) {
                throw ApiException(response.code, response.message ?: "服务器返回 null msg")
            } else {
                // 请求成功则发出请求结果
                emit(Res.Success(response.data))
            }
        }
            .flowOn(Dispatchers.IO) // 在 IO 线程执行
            .retryWhen { cause, attempt ->
                Log.d("BaseRepository#requestResponse-retryWhen", cause.message.toString())
                attempt < retry // 少于重试次数则重试
            }
            .catch { cause ->
                // 重试次数过后的异常会被此处捕获，处理
                Log.d("BaseRepository#requestResponse-catch", cause.message.toString())
                emit(Res.Error(ExceptionHandler.handle(cause)))
            }
    }
}