package com.sll.lib_common.entity.vo

import android.graphics.Bitmap
import com.sll.lib_common.entity.Sex

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/14
*/
data class UserVO(
    var username: String?, // 用户名
    var sex: Sex, // 性别
    var lastUpdateTime: Long, // 最后更新事件
    var createTime: Long,
    var avatar: String?,
    var introduce: String?
)

