package com.edipo2s.gistcomment.model

import okhttp3.Interceptor
import okhttp3.Response

internal class CredentialsInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)
        return response
    }

}