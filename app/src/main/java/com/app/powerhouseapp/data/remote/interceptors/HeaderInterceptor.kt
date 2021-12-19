package com.app.powerhouseapp.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val builder = chain.request().newBuilder()
        builder.header("Accept", "application/json")
        builder.header("Content-Type", "application/json")

        return chain.proceed(builder.build())
    }

}