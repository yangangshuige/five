package com.tool.bl53.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.didi.bike.applicationholder.AppContextHolder

object NetUtil {
    fun isConnected(): Boolean {
        val cm = AppContextHolder.applicationContext<Application>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val networkCapabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ?: false
        } else {
            val networkInfo = cm.activeNetworkInfo
            networkInfo?.isConnected ?: false
        }
    }
}