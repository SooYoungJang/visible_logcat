package com.sooyoungjang.debuglibrary.domain.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlin.coroutines.CoroutineContext

internal abstract class UseCase<Type : Any, in Params> : CoroutineScope {

    protected open fun invoke(params: Params): Flow<Type> { return emptyFlow() }

    protected open suspend fun run(params: Params) {}

    protected open fun exec(params: Params): Type? { return null }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Main
}