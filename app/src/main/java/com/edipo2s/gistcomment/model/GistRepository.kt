package com.edipo2s.gistcomment.model

import android.arch.lifecycle.LiveData
import com.edipo2s.gistcomment.model.entity.Gist
import com.edipo2s.gistcomment.model.entity.GistFile
import com.edipo2s.gistcomment.model.remote.GistRsp
import com.edipo2s.gistcomment.model.remote.IGistRemoteSource
import com.edipo2s.gistcomment.network.resource.ApiResponse
import com.edipo2s.gistcomment.network.resource.Resource
import com.edipo2s.gistcomment.network.resource.SimpleNetworkBoundResource
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

}