package com.edipo2s.gistcomment.model.remote

internal data class GistRsp(

        val id: String,
        val files: Map<String, GistRspFile>

)

internal data class GistRspFile(

        val filename: String,
        val language: String,
        val content: String

)