package com.edipo2s.gistcomment.model

import android.arch.lifecycle.LiveData
import com.edipo2s.gistcomment.model.entity.Gist
import com.edipo2s.gistcomment.model.entity.GistComment
import com.edipo2s.gistcomment.model.entity.GistFile
import com.edipo2s.gistcomment.model.remote.GistCommentRsp
import com.edipo2s.gistcomment.model.remote.GistRsp
import com.edipo2s.gistcomment.model.remote.IGistRemoteSource
import com.edipo2s.gistcomment.network.resource.ApiResponse
import com.edipo2s.gistcomment.network.resource.Resource
import com.edipo2s.gistcomment.network.resource.SimpleNetworkBoundResource
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

internal class GistRepository @Inject constructor(private val gistRemoteSource: IGistRemoteSource) {

    fun getGist(id: String): LiveData<Resource<Gist>> {
        return object : SimpleNetworkBoundResource<Gist, GistRsp>() {
            override fun parseCallResult(item: GistRsp): Gist {
                return Gist(item.id, item.files.map {
                    GistFile(it.value.filename, it.value.content)
                })
            }

            override fun createCall(): LiveData<ApiResponse<GistRsp>> {
                return gistRemoteSource.getGist(id)
            }

        }.asLiveData()
    }

    fun getGistComments(id: String): LiveData<Resource<List<GistComment>>> {
        return object : SimpleNetworkBoundResource<List<GistComment>, List<GistCommentRsp>>() {
            override fun parseCallResult(item: List<GistCommentRsp>): List<GistComment> {
                return item.map {
                    GistComment(it.id, it.user.login, it.user.avatar_url,
                            it.body, LocalDateTime.parse(it.created_at, DateTimeFormatter.ISO_DATE_TIME))
                }
            }

            override fun createCall(): LiveData<ApiResponse<List<GistCommentRsp>>> {
                return gistRemoteSource.getGistComments(id)
            }

        }.asLiveData()
    }

}