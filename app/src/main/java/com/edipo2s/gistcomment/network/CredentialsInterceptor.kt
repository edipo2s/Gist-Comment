package com.edipo2s.gistcomment.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class CredentialsInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)
        return response
    }

}