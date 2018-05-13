package com.edipo2s.gistcomment.model.remote

internal data class GistCommentRsp(

        val id: String,
        val user: GistUserRsp,
        val body: String,
        val created_at: String

)

internal data class GistUserRsp(

        val login: String,
        val avatar_url: String

)