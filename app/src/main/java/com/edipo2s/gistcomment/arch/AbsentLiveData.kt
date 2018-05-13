package com.edipo2s.gistcomment.arch

import android.arch.lifecycle.MutableLiveData

/**
 * A LiveData class that has {@code null} value.
 *
 * @see <a href="https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/util/AbsentLiveData.java">Source</a>
 */
class AbsentLiveData<T> private constructor() : MutableLiveData<T>() {

    init {
        postValue(null)
    }

    companion object {
        fun <T> create(): MutableLiveData<T> {
            return AbsentLiveData()
        }
    }
}
