package com.edipo2s.gistcomment.network

import android.content.SharedPreferences
import com.edipo2s.gistcomment.Constants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class CredentialsInterceptor @Inject constructor(private val sharedPrefs: SharedPreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequestBuilder = originalRequest.newBuilder()
                .method(originalRequest.method(), originalRequest.body())

        val headersNames = originalRequest.headers().names()
        if (headersNames.contains(Constants.AUTHORIZATION_HEADER) &&
                sharedPrefs.contains(Constants.PREF_OAUTH_KEY)) {
            val oauthToken = sharedPrefs.getString(Constants.PREF_OAUTH_KEY, "")
            newRequestBuilder.header(Constants.AUTHORIZATION_HEADER, "token $oauthToken")
        }

        return chain.proceed(newRequestBuilder.build())
    }

}