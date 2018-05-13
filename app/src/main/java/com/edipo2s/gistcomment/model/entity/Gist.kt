package com.edipo2s.gistcomment.model.entity

internal data class Gist(

        val id: String,
        val files: List<GistFile>

)

internal data class GistFile(

        val name: String,
        val content: String

)