@file:Suppress("PropertyName")

package com.cjapps.prop

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

interface IDispatcherProvider {
    val IO: CoroutineDispatcher
    val Main: CoroutineDispatcher
    val Default: CoroutineDispatcher
}

class DispatcherProvider @Inject constructor() : IDispatcherProvider {
    override val IO: CoroutineDispatcher
        get() = Dispatchers.IO
    override val Main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val Default: CoroutineDispatcher
        get() = Dispatchers.Default
}