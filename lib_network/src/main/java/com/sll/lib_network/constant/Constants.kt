package com.sll.lib_network.constant

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/02
 */

const val BASE_URL_MAIN = "http://47.107.52.7:88/member/photo/"

const val BASE_URL_YIYAN = "https://v1.hitokoto.cn/"

const val BASE_URL_IMAGE = "https://img.paulzzh.com/touhou/random/"

const val APP_ID = "00354a923416474da1d6e2ecb64833ba"

const val APP_SECRET = "94058f5102a9c813c49f8bd2fe7f4eec3c8b4"

const val APP_ID_LIN = "1f43d942186848fb87eeb76dcaee2872"

const val APP_SECRET_LIN = "1309564bd23cd775a4405948db1c45ff64d83"

const val APP_ID_LI = "7be86e8dbad845e09bc4673e4b78744d"

const val APP_SECRET_LI = "975087f883584575142478225760ae4eff6bc"

/** 请求成功 */
const val CODE_OK = 200

/** 未授权 */
const val CODE_UNAUTHORIZED = 401

/** 资源找不到 */
const val CODE_RESOURCE_NOT_FOUND = 404

/** 服务器内部错误 */
const val CODE_SERVER_ERROR = 500

/** 没有或缺少 appId 或 appSecret */
const val CODE_LACK_APP_KEY = 5217

/** 接口使用次数不够 */
const val CODE_LACK_INTERFACE = 5311

/** 请求参数不对 */
const val CODE_QUERY_PARAMS_ERROR = 5314

// 未登录
const val CODE_UNLOGIN = -1001

// 未知错误
const val CODE_UNKNOWN = 1000

// 解析错误
const val CODE_PARSE_ERROR = 1001

// 网络错误
const val CODE_NETWORK_ERROR = 1002

// 协议出错
const val CODE_HTTP_ERROR = 1003

// 证书出错
const val CODE_SSL_ERROR = 1004

// 连接超时
const val CODE_TIMEOUT_ERROR = 1006

// 未知 host
const val CODE_UNKNOWN_HOST = 1007