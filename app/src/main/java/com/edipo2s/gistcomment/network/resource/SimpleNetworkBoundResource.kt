package com.edipo2s.gistcomment.network.resource

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.edipo2s.gistcomment.arch.AbsentLiveData

/**
 * A generic class that can provide a resource backed just for the network with ShouldFetch always true
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>.
 * @param <ResultType>
 * @param <RequestRspType>
 *
 */
abstract class SimpleNetworkBoundResource<ResultType, RequestRspType> @MainThread constructor() :
        NetworkBoundResource<ResultType, RequestRspType>() {

    private var dbLiveData: MutableLiveData<ResultType> = AbsentLiveData.create()

    @WorkerThread
    protected abstract fun parseCallResult(item: RequestRspType): ResultType

    @MainThread
    override fun loadFromDb(): LiveData<ResultType> = dbLiveData

    @WorkerThread
    override fun saveCallResult(item: RequestRspType) {
        dbLiveData.postValue(parseCallResult(item))
    }

    @MainThread
    override fun shouldFetch(data: ResultType?): Boolean = true

}