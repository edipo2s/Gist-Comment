/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.edipo2s.gistcomment

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 *
 * @see <a href="https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/AppExecutors.java">Source</a>
 */
private val MAIN_EXECUTOR = object : Executor {
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        mainThreadHandler.post(command)
    }

    fun executeDelayed(command: () -> Unit, delay: Long) {
        mainThreadHandler.postDelayed(command, delay)
    }
}

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

val NETWORK_EXECUTOR: ExecutorService = Executors.newFixedThreadPool(3)

fun ioThread(block: () -> Unit) {
    IO_EXECUTOR.execute(block)
}

fun networkThread(block: () -> Unit) {
    NETWORK_EXECUTOR.execute(block)
}

fun uiThread(delay: Long = 0L, block: () -> Unit) {
    if (delay == 0L) {
        MAIN_EXECUTOR.execute(block)
    } else {
        MAIN_EXECUTOR.executeDelayed(block, delay)
    }
}