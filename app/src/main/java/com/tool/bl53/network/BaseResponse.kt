package com.tool.bl53.network

import java.io.Serializable


data class BaseResponse<out T>(val message: String, val code: Int, val data: T) : Serializable