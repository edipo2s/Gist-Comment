package com.edipo2s.gistcomment.model.remote

import android.arch.lifecycle.LiveData
import com.edipo2s.gistcomment.network.resource.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface IGistRemoteSource {

    companion object {

        private const val PATH_GIST_ID = "id"

    }

    @GET("gists/{$PATH_GIST_ID}")
    fun getGist(@Path(PATH_GIST_ID) id: String): LiveData<ApiResponse<GistRsp>>

}