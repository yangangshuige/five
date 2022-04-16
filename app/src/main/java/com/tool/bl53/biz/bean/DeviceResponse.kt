package com.tool.bl53.biz.bean

import java.io.Serializable

data class DeviceResponse(
    val errMsg: String,
    val securityInfoVO: SecurityInfoVO,
    val state: Boolean
) : Serializable

data class SecurityInfoVO(
    val deviceId: String?,
    val key: String?,
    val mac: String?,
    val passwd: String?,
    val secret: String?
) : Serializable {
    fun available(): Boolean {
        return key?.isNotEmpty() == true && passwd?.isNotEmpty() == true && mac?.isNotEmpty() == true
    }
}