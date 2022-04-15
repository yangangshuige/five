package com.tool.bl53.network

import com.tool.bl53.api.Api
import com.tool.bl53.api.ApiConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class JhRetrofitFactory private constructor() :
    BaseRetrofitFactory() {
    val service by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        getService(Api::class.java)
    }

    companion object {
        val instance: JhRetrofitFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            JhRetrofitFactory()
        }
//        private var instance: RetrofitFactory? = null
//        fun getInStance(hostType: Int = ANDROID_URL) = instance ?: synchronized(this) {
//            instance ?: RetrofitFactory(hostType).also { instance = it }
//        }
    }

    override fun handleBuilder(builder: OkHttpClient.Builder) {
    }

    override fun retrofitBuilder(builder: Retrofit.Builder) {

    }
}