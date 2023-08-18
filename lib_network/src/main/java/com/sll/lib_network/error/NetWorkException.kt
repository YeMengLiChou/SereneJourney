package com.sll.lib_network.error

/**
 * 网络错误类
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */
open class EmptyDataException(
    val code: Int,
    val msg: String,
    e: Throwable? = null
) : Exception(e) {

    constructor(error: ERROR, e: Throwable? = null) : this(error.code, error.msg, e)

}