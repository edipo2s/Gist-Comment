package com.edipo2s.gistcomment.network.resource

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.edipo2s.gistcomment.ioThread
import com.edipo2s.gistcomment.uiThread

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>.
 * @param <ResultType>
 * @param <RequestRspType>
 *
 * @see <a href="https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/repository/NetworkBoundResource.java">Source</a>
 */
abstract class NetworkBoundResource<ResultType, RequestRspType> @MainThread constructor() {

    private val result = object : MediatorLiveData<Resource<ResultType>>() {
        override fun setValue(newValue: Resource<ResultType>?) {
            if (newValue != value) {
                super.setValue(newValue)
            }
        }
    }

    init {
        result.value = Resource.loading()
        uiThread {
            val dbSource = loadFromDb()
            result.addSource(dbSource) { resultType ->
                result.removeSource(dbSource)
                if (shouldFetch(resultType)) {
                    fetchFromNetwork(dbSource)
                } else {
                    result.addSource(dbSource) { newResultType ->
                        result.value = Resource.success(newResultType)
                    }
                }
            }
        }
    }

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestRspType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestRspType>>

    protected open fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<ResultType>> = result

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { resultType ->
            result.value = Resource.loading(resultType)
        }

        val apiResponse = createCall()
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            if (response?.isSuccessful == true) {
                ioThread {
                    processResponse(response)?.let {
                        saveCallResult(it)
                    }
                    uiThread {
                        // we specially request a new live data, otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(loadFromDb()) { resultType ->
                            result.value = Resource.success(resultType)
                        }
                    }
                }

            } else {
                onFetchFailed()
                result.addSource(dbSource) { resultType ->
                    result.value = response?.let {
                        Resource.error(it.code, "${it.errorMessage}", resultType)
                    }
                }
            }
        }
    }

    @WorkerThread
    private fun processResponse(response: ApiResponse<RequestRspType>): RequestRspType? = response.body

}