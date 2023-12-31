package com.sll.lib_framework.ext


import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.addAdapter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Moshi 扩展类
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */


val moshi: Moshi by lazy {
    Moshi.Builder()
        .build()
}

/**
 * 转为 Json
 * @param indent pretty 就传入 "\t"
 * */
inline fun <reified T> T.toJson(indent: String = ""): String? {
    try {
        val jsonAdapter = moshi.adapter<T>(getGenericType<T>())
        return jsonAdapter.indent(indent).toJson(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/**
 * 从 Json 转为 对象实例
 * */
inline fun <reified T> String.fromJson(): T? {
    try {
        val jsonAdapter = moshi.adapter<T>(getGenericType<T>())
        return jsonAdapter.fromJson(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

abstract class MoshiTypeReference<T> // 自定义的类，用来包装泛型

inline fun <reified T> getGenericType(): Type {
    return (object :
        MoshiTypeReference<T>() {}::class.java)
        .genericSuperclass
        .let { it as ParameterizedType }
        .actualTypeArguments
        .first()
}

