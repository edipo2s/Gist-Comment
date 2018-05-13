package com.edipo2s.gistcomment.network.resource

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread

/**
 * A generic class that can provide a resource backed just for the network without response data
 */
abstract class AbsentNetworkBoundResource @MainThread constructor() {

    private val result = object : MediatorLiveData<Resource<Void?>>() {
        override fun setValue(newValue: Resource<Void?>?) {
            if (newValue != value) {
                super.setValue(newValue)
            }
        }
    }

    init {
        result.value = Resource.loading()
        fetchFromNetwork()
    }

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<Void?>>

    protected open fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<Void?>> = result

    private fun fetchFromNetwork() {
        val apiResponse = createCall()
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            if (response?.isSuccessful == true) {
                result.value = Resource.success(null)
            } else {
                onFetchFailed()
                result.value = response?.let {
                    Resource.error(it.code, "${it.errorMessage}", null)
                }
            }
        }
    }

}