package com.sll.lib_framework.ext

import java.lang.ClassCastException
import java.lang.Exception

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */


/**
 * 不检查的类型转换
 * */
@Suppress("UNCHECKED_CAST")
fun <T> Any.uncheckAs(): T {
    return this as T
}

/**
 * 直接转
 * */
inline fun <reified T> Any.As(): T? {
    return this as? T
}



/**
 * 带有检查的类型转换
 * */
inline fun <reified T> Any.safeAs(crossinline onError: (Exception) -> Unit = {}): T? {
    return try {
        this as T
    } catch (e: ClassCastException) {
        onError.invoke(e)
        null
    }
}