package com.tool.bl53.network

import android.widget.Toast
import com.didi.bike.applicationholder.AppContextHolder
import com.tool.bl53.utils.NetUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Desc : 监听网络变化，只在联网状态下发起请求
 */
class NetworkStatusInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetUtil.isConnected()) {
            Toast.makeText(AppContextHolder.applicationContext(), "网络未连接", Toast.LENGTH_SHORT).show()
        }
        return chain.proceed(chain.request())
    }
}