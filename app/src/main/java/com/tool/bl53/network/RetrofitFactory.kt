package com.tool.bl53.network

import com.tool.bl53.api.Api
import com.tool.bl53.api.ApiConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class RetrofitFactory :
    BaseRetrofitFactory() {
    val service by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        getService(Api::class.java)
    }

    companion object {
        val instance: RetrofitFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitFactory()
        }
    }

    override fun handleBuilder(builder: OkHttpClient.Builder) {
    }

    override fun retrofitBuilder(builder: Retrofit.Builder) {

    }
}