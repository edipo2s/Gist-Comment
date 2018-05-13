package com.edipo2s.gistcomment.model.entity

import org.threeten.bp.LocalDateTime

internal data class GistComment(

        val id: String,
        val user_name: String,
        val user_avatar: String,
        val content: String,
        val date: LocalDateTime

)