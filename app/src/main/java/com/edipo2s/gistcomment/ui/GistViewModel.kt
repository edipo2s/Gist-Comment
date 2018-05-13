package com.edipo2s.gistcomment.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.edipo2s.gistcomment.model.GistRepository
import com.edipo2s.gistcomment.model.entity.Gist
import com.edipo2s.gistcomment.model.entity.GistComment
import com.edipo2s.gistcomment.network.resource.Resource
import javax.inject.Inject

internal class GistViewModel @Inject constructor(private val gistRepository: GistRepository,
                                                 app: Application) : AndroidViewModel(app) {

    private val gistIdLiveData = MutableLiveData<String>()

    val gistLiveData: LiveData<Resource<Gist>> = Transformations.switchMap(gistIdLiveData) {
        gistRepository.getGist(it)
    }

    val gistCommentsLiveData: LiveData<Resource<List<GistComment>>> = Transformations.switchMap(gistIdLiveData) {
        gistRepository.getGistComments(it)
    }

    fun requestGist(id: String) {
        gistIdLiveData.value = id
    }

}