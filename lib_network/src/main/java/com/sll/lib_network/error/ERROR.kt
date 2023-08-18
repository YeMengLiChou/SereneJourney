package com.sll.lib_network.error

import com.sll.lib_network.constant.CODE_HTTP_ERROR
import com.sll.lib_network.constant.CODE_LACK_APP_KEY
import com.sll.lib_network.constant.CODE_LACK_INTERFACE
import com.sll.lib_network.constant.CODE_NETWORK_ERROR
import com.sll.lib_network.constant.CODE_PARSE_ERROR
import com.sll.lib_network.constant.CODE_QUERY_PARAMS_ERROR
import com.sll.lib_network.constant.CODE_RESOURCE_NOT_FOUND
import com.sll.lib_network.constant.CODE_SERVER_ERROR
import com.sll.lib_network.constant.CODE_SSL_ERROR
import com.sll.lib_network.constant.CODE_TIMEOUT_ERROR
import com.sll.lib_network.constant.CODE_UNAUTHORIZED
import com.sll.lib_network.constant.CODE_UNKNOWN
import com.sll.lib_network.constant.CODE_UNKNOWN_HOST
import com.sll.lib_network.constant.CODE_UNLOGIN

/**
 * 网络错误类，对应 接口 HTTP 的状态码
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */
enum class ERROR(val code: Int, val msg: String) {
    /**
     * 当前请求需要用户验证
     */
    UNAUTHORIZED(CODE_UNAUTHORIZED, "当前请求需要用户验证"),


    /**
     * 无法找到指定位置的资源
     */
    NOT_FOUND(CODE_RESOURCE_NOT_FOUND, "无法找到指定位置的资源"),


    /**
     * 服务器遇到了意料不到的情况，不能完成客户的请求
     */
    SERVER_ERROR(CODE_SERVER_ERROR, "服务器错误"),

    /**
     * 没有或缺少 appId 或 appSecret
     */
    LACK_APP_KEY(CODE_LACK_APP_KEY, "没有或缺少 appId 或 appSecret"),

    /**
     * 接口使用次数不够
     */
    LACK_INTERFACE(CODE_LACK_INTERFACE, "接口使用次数不够"),

    /**
     * 请求参数不对
     */
    QUERY_PARAMS_ERROR(CODE_QUERY_PARAMS_ERROR, "请求参数不对"),

    /**
     * 未知错误
     */
    UNKNOWN(CODE_UNKNOWN, "未知错误"),

    /**
     * 解析错误
     */
    PARSE_ERROR(CODE_PARSE_ERROR, "解析错误"),

    /**
     * 网络错误
     */
    NETWORK_ERROR(CODE_NETWORK_ERROR, "网络异常，请尝试刷新"),

    /**
     * 协议出错
     */
    HTTP_ERROR(CODE_HTTP_ERROR, "404 Not Found"),

    /**
     * 证书出错
     */
    SSL_ERROR(CODE_SSL_ERROR, "证书出错"),

    /**
     * 连接超时
     */
    TIMEOUT_ERROR(CODE_TIMEOUT_ERROR, "连接超时"),

    /**
     * 未登录
     */
    UNLOGIN(CODE_UNLOGIN, "未登录"),

    /**
     * 未知 Host
     */
    UNKNOWN_HOST(CODE_UNKNOWN_HOST, "未知Host");
}