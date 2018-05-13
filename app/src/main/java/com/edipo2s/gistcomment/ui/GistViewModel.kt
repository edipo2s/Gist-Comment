package com.edipo2s.gistcomment.ui

import android.app.Application
import android.arch.lifecycle.*
import android.content.SharedPreferences
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import com.edipo2s.gistcomment.BuildConfig
import com.edipo2s.gistcomment.Constants
import com.edipo2s.gistcomment.arch.SingleMutableLiveData
import com.edipo2s.gistcomment.model.GistRepository
import com.edipo2s.gistcomment.model.entity.Gist
import com.edipo2s.gistcomment.model.entity.GistComment
import com.edipo2s.gistcomment.network.resource.Resource
import javax.inject.Inject

internal class GistViewModel @Inject constructor(private val gistRepository: GistRepository,
                                                 private val sharedPrefs: SharedPreferences,
                                                 private val app: Application) : AndroidViewModel(app) {

    private val gistIdLiveData = MutableLiveData<String>()
    private val gistAuthCodeLiveData = MutableLiveData<String>()
    private val gistIdCommentLiveData = MutableLiveData<Pair<String, String>>()
    private val gistUpdateCommentsLiveData = SingleMutableLiveData<String>()

    private val _gistCommentsLiveData = Transformations.switchMap(gistIdLiveData) {
        gistRepository.getGistComments(it)
    }

    private val _gistUpdateCommentsLiveData = Transformations.switchMap(gistUpdateCommentsLiveData) {
        gistRepository.getGistComments(it)
    }

    val gistLiveData: LiveData<Resource<Gist>> = Transformations.switchMap(gistIdLiveData) {
        gistRepository.getGist(it)
    }

    val gistAuthTokenLiveData: LiveData<Resource<String>> = Transformations.switchMap(gistAuthCodeLiveData) {
        gistRepository.getGithubAuthToken(it)
    }

    val gistCommentsLiveData: LiveData<Resource<List<GistComment>>> = MediatorLiveData<Resource<List<GistComment>>>().apply {
        addSource(_gistCommentsLiveData) {
            it?.let { value = it }
        }
        addSource(_gistUpdateCommentsLiveData) {
            it?.let { value = it }
        }
    }

    val newGistCommentLiveData: LiveData<Resource<GistComment>> = Transformations.switchMap(gistIdCommentLiveData) {
        gistRepository.createGistComments(it.first, it.second)
    }

    override fun onCleared() {
        super.onCleared()
        with(gistCommentsLiveData as MediatorLiveData) {
            removeSource(_gistCommentsLiveData)
            removeSource(_gistUpdateCommentsLiveData)
        }
    }

    fun requestGist(id: String) {
        gistIdLiveData.value = id
        if (sharedPrefs.contains(Constants.PREF_OAUTH_KEY)) {
            val successOAuthToken = Resource.success(sharedPrefs.getString(Constants.PREF_OAUTH_KEY, ""))
            (gistAuthTokenLiveData as MutableLiveData<Resource<String>>).value = successOAuthToken
        }
    }

    fun getGitAuthCode(data: Uri) {
        gistAuthCodeLiveData.value = data.getQueryParameter("code")
    }

    fun sendCommentOrStartSign(comment: String) {
        if (gistAuthTokenLiveData.value == null) {
            val githubAuthUri = Uri.Builder()
                    .scheme("https")
                    .path("github.com/login/oauth/authorize")
                    .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                    .appendQueryParameter("scope", "gist")
                    .build()
            CustomTabsIntent.Builder()
                    .build()
                    .launchUrl(app, githubAuthUri)
        } else {
            gistIdLiveData.value?.let { gistId ->
                gistIdCommentLiveData.value = gistId to comment
            }
        }
    }

    fun updateComments() {
        gistIdLiveData.value?.let { gistId ->
            gistUpdateCommentsLiveData.call(gistId)
        }
    }

}