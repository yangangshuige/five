package com.tool.bl53.network

import java.io.Serializable


data class JhResponse<out T>(val reason: String, val error_code: Int, val result: T) : Serializable