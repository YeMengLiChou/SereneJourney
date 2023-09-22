package com.sll.lib_network.ext

import androidx.lifecycle.ViewModel
import com.sll.lib_framework.helper.AppHelper
import com.sll.lib_framework.util.debug
import com.sll.lib_framework.util.error
import com.sll.lib_network.error.ApiException
import com.sll.lib_network.error.ERROR
import com.sll.lib_network.error.ExceptionHandler
import com.sll.lib_network.response.Res
import com.sll.lib_network.response.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */

/**
 * 标准 Response 格式的请求方法
 *
 * code、msg、data
 * */
suspend fun <T> ViewModel.requestResponse(
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
            throw ApiException(response.code, response.message ?: "null")
        } else {
            // 请求成功则发出 返回结果
            emit(Res.Success(response.data))
        }
    }
        .flowOn(Dispatchers.IO) // 在 IO 线程执行
        .retryWhen { cause, attempt ->
            error("requestResponse-retry${attempt}", "cause: ${cause.message.toString()}")
            attempt < retry // 少于重试次数则重试
        }
        .catch { cause ->
            // 重试次数过后的异常会被此处捕获，处理
            error("requestResponse-catch", "cause:${cause.message.toString()}")
            cause.printStackTrace()
            emit(Res.Error(ExceptionHandler.handle(cause)))
        }
}

/**
 * 直接请求
 * */
fun <T> ViewModel.request(
    timeout: Long = -1L,
    retry: Int = 0,
    request: suspend () -> T,
): Flow<Res<T>> {
    var loading = false

    return flow {
        // 请求发送中可以加载弹窗
        if (!loading) { // 保证重试时仅发送一次 Loading
            loading = true
            emit(Res.Loading)
        }
        val response: T? = if (timeout > 0) { // 设置超时
            try {
                withTimeout(timeout) { request() }
            } catch (e: TimeoutCancellationException) { // 捕捉超时错误
                null
            }
        } else {
            request()
        }

        // 请求失败
        if (response == null) {
            throw ApiException(ERROR.NETWORK_ERROR)
        } else {
            // 请求成功则发出请求结果
            emit(Res.Success(response))
        }
    }
        .flowOn(Dispatchers.IO) // 在 IO 线程执行
        .retryWhen { cause, attempt ->
            error("requestResponse-retryWhen:${attempt}", cause.message.toString())
            attempt < retry // 少于重试次数则重试
        }
        .catch { cause ->
            // 重试次数过后的异常会被此处捕获，处理
            error("requestResponse-catch", cause.message.toString())
            emit(Res.Error(ExceptionHandler.handle(cause)))
        }
}