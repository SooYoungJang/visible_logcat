package com.sooyoungjang.debuglibrary.di

import android.content.Context
import android.content.SharedPreferences
import com.sooyoungjang.debuglibrary.data.AppDatabase
import com.sooyoungjang.debuglibrary.data.datastore.DataStoreRepositoryImpl
import com.sooyoungjang.debuglibrary.data.datastore.local.DataStoreLocalDataSourceImpl
import com.sooyoungjang.debuglibrary.data.log.LogRepositoryImpl
import com.sooyoungjang.debuglibrary.data.log.local.LogLocalDataSource
import com.sooyoungjang.debuglibrary.data.log.local.LogLocalDataSourceImpl
import com.sooyoungjang.debuglibrary.data.log.remote.LogRemoteDataSourceImpl
import com.sooyoungjang.debuglibrary.data.log.remote.LogcatCollector
import com.sooyoungjang.debuglibrary.domain.datastore.usecase.GetNeverSeeAgainUseCase
import com.sooyoungjang.debuglibrary.domain.datastore.usecase.WriteDataStoreUseCase
import com.sooyoungjang.debuglibrary.domain.log.LogRepository
import com.sooyoungjang.debuglibrary.domain.log.usecase.ClearLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.DeleteLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.sooyoungjang.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_DEBUG_TOOL
import com.sooyoungjang.debuglibrary.util.ResourceProvider
import com.sooyoungjang.debuglibrary.util.ResourceProviderImpl
import com.sooyoungjang.debuglibrary.util.SharedPreferencesUtil

internal class AppContainer(context: Context) {

    private val logcatCollector = LogcatCollector()
    private val logRemoteDataSource = LogRemoteDataSourceImpl(logcatCollector)

    private val appDataBase: AppDatabase by lazy { AppDatabase.getInstance(context) }
    private val logLocalDataSource: LogLocalDataSource by lazy { LogLocalDataSourceImpl(appDataBase.logDao()) }

    private val logRepository: LogRepository by lazy { LogRepositoryImpl(logRemoteDataSource, logLocalDataSource) }

    val resourceProvider: ResourceProvider by lazy { ResourceProviderImpl(context.resources) }
    val getLogcatUseCase: GetLogcatUseCase by lazy { GetLogcatUseCase(logRepository) }
    val clearLogUseCase: ClearLogUseCase by lazy { ClearLogUseCase(logRepository) }
    val deleteLogUseCase: DeleteLogUseCase by lazy { DeleteLogUseCase(logRepository) }

    private val dataStoreLocalDataSource: DataStoreLocalDataSourceImpl by lazy { DataStoreLocalDataSourceImpl(sharedPreferences) }
    private val dataStoreRepository: DataStoreRepositoryImpl by lazy { DataStoreRepositoryImpl(dataStoreLocalDataSource) }

    private val sharedPreferences: SharedPreferences by lazy { context.getSharedPreferences(EDDY_DEBUG_TOOL, Context.MODE_PRIVATE) }
    val sharedPreferencesUtil: SharedPreferencesUtil by lazy { SharedPreferencesUtil(sharedPreferences) }

    val getNeverSeeAgainUseCase: GetNeverSeeAgainUseCase by lazy { GetNeverSeeAgainUseCase(dataStoreRepository) }
    val writeDataStoreUseCase: WriteDataStoreUseCase by lazy { WriteDataStoreUseCase(dataStoreRepository) }

}