package com.eddy.debuglibrary.di

import android.content.Context

internal class DiManager private constructor(context: Context) {

    val appContainer = AppContainer(context)

    companion object : SingletonHolder<DiManager, Context>(::DiManager)
}