package com.edipo2s.gistcomment.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import androidx.core.widget.toast
import com.edipo2s.gistcomment.BuildConfig
import com.edipo2s.gistcomment.model.GistRepository
import com.edipo2s.gistcomment.model.entity.Gist
import com.edipo2s.gistcomment.model.entity.GistComment
import com.edipo2s.gistcomment.network.resource.Resource
import javax.inject.Inject

internal class GistViewModel @Inject constructor(private val gistRepository: GistRepository,
                                                 private val app: Application) : AndroidViewModel(app) {

    private val gistIdLiveData = MutableLiveData<String>()
    private val gistAuthCodeLiveData = MutableLiveData<String>()

    val gistLiveData: LiveData<Resource<Gist>> = Transformations.switchMap(gistIdLiveData) {
        gistRepository.getGist(it)
    }

    val gistAuthTokenLiveData: LiveData<Resource<String>> = Transformations.switchMap(gistAuthCodeLiveData) {
        gistRepository.getGithubAuthToken(it)
    }

    val gistCommentsLiveData: LiveData<Resource<List<GistComment>>> = Transformations.switchMap(gistIdLiveData) {
        gistRepository.getGistComments(it)
    }

    fun requestGist(id: String) {
        gistIdLiveData.value = id
    }

    fun sendCommentOrStartSign(comment: String) {
        if (gistAuthTokenLiveData.value == null) {
            val url = "https://github.com/login/oauth/authorize?client_id=${BuildConfig.GITHUB_CLIENT_ID}"
            CustomTabsIntent.Builder()
                    .build()
                    .launchUrl(app, Uri.parse(url))
        } else {
            app.toast("Comment Sent: ${gistAuthTokenLiveData.value}")
        }
    }

    fun getGitAuthCode(data: Uri) {
        gistAuthCodeLiveData.value = data.getQueryParameter("code")
    }

}