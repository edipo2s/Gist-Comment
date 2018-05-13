package com.edipo2s.gistcomment.model.remote

import android.arch.lifecycle.LiveData
import com.edipo2s.gistcomment.Constants
import com.edipo2s.gistcomment.network.resource.ApiResponse
import retrofit2.http.*

internal interface IGistRemoteSource {

    companion object {

        private const val PATH_GIST_ID = "id"

    }

    @GET("gists/{$PATH_GIST_ID}")
    fun getGist(@Path(PATH_GIST_ID) id: String): LiveData<ApiResponse<GistRsp>>

    @GET("gists/{$PATH_GIST_ID}/comments")
    fun getGistComments(@Path(PATH_GIST_ID) id: String): LiveData<ApiResponse<List<GistCommentRsp>>>

    @Headers(Constants.REQUEST_AUTHORIZATION)
    @POST("gists/{$PATH_GIST_ID}/comments")
    fun createGistComment(@Path(PATH_GIST_ID) id: String,
                          @Body body: GistCommentRqt): LiveData<ApiResponse<GistCommentRsp>>

}