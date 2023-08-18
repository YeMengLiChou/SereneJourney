package com.sll.lib_common.entity.dto

import com.squareup.moshi.JsonClass

/**
 * 分页实体
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */
@JsonClass(generateAdapter = true)
data class Paging <out T> (
    // 共多少条
    val total: Int,
    // 页面大小
    val size: Int,
    // 当前页
    val current: Int,
    // 数据列表
    val records: List<T>
)
