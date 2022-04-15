package com.tool.bl53.network

import java.io.Serializable

data class BaseListResponse<T>(
    val offset: Int,
    val size: Int,
    val total: Int,
    val pageCount: Int,
    val curPage: Int,
    val over: Boolean,
    val datas: T
) : Serializable