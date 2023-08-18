package com.sll.lib_common.entity.encoder

import com.sll.lib_common.entity.dto.LoginInfo
import com.sll.lib_common.entity.dto.User
import com.sll.lib_framework.ext.fromJson
import com.sll.lib_framework.ext.toJson
import io.fastkv.interfaces.FastEncoder

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
object LoginInfoEncoder: FastEncoder<LoginInfo> {

        override fun tag(): String {
            return "LoginInfo"
        }

        override fun encode(obj: LoginInfo): ByteArray {
            return obj.toJson()!!.encodeToByteArray()
        }

    override fun decode(bytes: ByteArray, offset: Int, length: Int): LoginInfo {
        return bytes.decodeToString(offset, offset + length).fromJson<LoginInfo>()!!
    }


    }