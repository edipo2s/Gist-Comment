package com.edipo2s.gistcomment.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import androidx.core.content.edit
import com.edipo2s.gistcomment.BuildConfig
import com.edipo2s.gistcomment.Constants
import com.edipo2s.gistcomment.model.entity.Gist
import com.edipo2s.gistcomment.model.entity.GistComment
import com.edipo2s.gistcomment.model.entity.GistFile
import com.edipo2s.gistcomment.model.remote.GistCommentRqt
import com.edipo2s.gistcomment.model.remote.GistCommentRsp
import com.edipo2s.gistcomment.model.remote.GistRsp
import com.edipo2s.gistcomment.model.remote.IGistRemoteSource
import com.edipo2s.gistcomment.network.resource.ApiResponse
import com.edipo2s.gistcomment.network.resource.Resource
import com.edipo2s.gistcomment.network.resource.SimpleNetworkBoundResource
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException
import javax.inject.Inject

internal class GistRepository @Inject constructor(private val gistRemoteSource: IGistRemoteSource,
                                                  private val okHttpClient: OkHttpClient,
                                                  private val sharedPrefs: SharedPreferences) {

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

    fun createGistComments(id: String, comment: String): LiveData<Resource<GistComment>> {
        return object : SimpleNetworkBoundResource<GistComment, GistCommentRsp>() {
            override fun parseCallResult(item: GistCommentRsp): GistComment {
                return GistComment(item.id, item.user.login, item.user.avatar_url,
                        item.body, LocalDateTime.parse(item.created_at, DateTimeFormatter.ISO_DATE_TIME))
            }

            override fun createCall(): LiveData<ApiResponse<GistCommentRsp>> {
                return gistRemoteSource.createGistComment(id, GistCommentRqt(comment))
            }

        }.asLiveData()
    }

    fun getGithubAuthToken(authCode: String): LiveData<Resource<String>> {
        val authTokenLiveData = MutableLiveData<Resource<String>>()
        authTokenLiveData.value = Resource.loading()
        val authTokenUrl = generateGithubAuthTokenUrl(authCode)
        if (authTokenUrl != null) {
            val request = Request.Builder()
                    .header("Accept", "application/json")
                    .url(authTokenUrl)
                    .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.body()?.string())
                            val oauthToken = jsonObject.getString("access_token")
                            sharedPrefs.edit {
                                putString(Constants.PREF_OAUTH_KEY, oauthToken)
                            }
                            authTokenLiveData.postValue(Resource.success(oauthToken))
                        } catch (e: JSONException) {
                            authTokenLiveData.postValue(Resource.error(0, e.message
                                    ?: "", ""))
                        }
                    } else {
                        authTokenLiveData.postValue(Resource.error(0, response.message(), ""))
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    authTokenLiveData.postValue(Resource.error(0, e.message ?: "", ""))
                }
            })
        } else {
            authTokenLiveData.value = Resource.error(0, "Github auth token Url parser error", "")
        }
        return authTokenLiveData
    }

    private fun generateGithubAuthTokenUrl(authCode: String): HttpUrl? {
        return HttpUrl.parse("https://github.com/login/oauth/access_token")
                ?.newBuilder()
                ?.addQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                ?.addQueryParameter("client_secret", BuildConfig.GITHUB_CLIENT_SECRET)
                ?.addQueryParameter("code", authCode)
                ?.build()
    }

}