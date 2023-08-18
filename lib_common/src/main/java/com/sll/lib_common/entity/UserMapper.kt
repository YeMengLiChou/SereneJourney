package com.sll.lib_common.entity

import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.entity.vo.UserVO

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/14
 */
object UserMapper {

    fun toVo(user: User): UserVO {
        val sex = when(user.sex) {
             1 -> Sex.MALE
             2 -> Sex.FEMALE
             else -> Sex.UNKNOWN
        }
        val bitmap = null


        return UserVO (
            username = user.username,
            avatar = null, // TODO: 转为Bitmap
            sex = sex,
            introduce = user.introduce,
            lastUpdateTime = user.lastUpdateTime,
            createTime = user.createTime
        )
    }
}