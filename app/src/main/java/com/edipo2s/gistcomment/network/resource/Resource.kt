package com.edipo2s.gistcomment.network.resource

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 *
 * @see <a href="https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/vo/Resource.java">Source</a>
 */
data class Resource<out T>(val status: Status, val data: T?, val errorCode: Int?, val errorMsg: String?) {

    fun <R> map(transform: ((T?) -> R)? = null): Resource<R> {
        return Resource(status, transform?.invoke(data), errorCode, errorMsg)
    }

    companion object {

        fun <T> success(data: T? = null): Resource<T> = Resource(Status.SUCCESS, data, null, null)

        fun <T> error(code: Int, msg: String, data: T? = null): Resource<T> = Resource(Status.ERROR, data, code, msg)

        fun <T> loading(data: T? = null): Resource<T> = Resource(Status.LOADING, data, null, null)

    }

}