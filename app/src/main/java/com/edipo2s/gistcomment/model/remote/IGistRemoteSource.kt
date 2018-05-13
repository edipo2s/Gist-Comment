package com.edipo2s.gistcomment.model.remote

import retrofit2.http.GET

internal interface IGistRemoteSource {

    @GET("")
    fun getGist()

}