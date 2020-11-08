package com.studita.di.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.studita.data.cache.chapter.ChaptersCacheImpl
import com.studita.data.cache.exercises.ExercisesCacheImpl
import com.studita.data.cache.interesting.InterestingCacheImpl
import com.studita.data.cache.levels.LevelsCacheImpl
import com.studita.data.net.OfflineDataService
import com.studita.data.repository.OfflineDataRepositoryImpl
import com.studita.data.repository.datasource.offline_data.CloudOfflineDataDataStore
import com.studita.data.repository.datasource.offline_data.DiskOfflineDataDataStore
import com.studita.di.CacheModule
import com.studita.di.DI
import com.studita.di.NetworkModule
import com.studita.di.NetworkModule.setProgressRetrofitListener
import com.studita.domain.interactor.offline_data.OfflineDataInteractor
import com.studita.domain.interactor.offline_data.OfflineDataInteractorImpl
import com.studita.domain.repository.OfflineDataRepository

object OfflineDataModule {

    private lateinit var config: DI.Config

    private var repository: OfflineDataRepository? = null
    private var offlineDataInteractor: OfflineDataInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getOfflineDataInteractorImpl(downloadListenerLiveData: MutableLiveData<Triple<Float, Float, Boolean>>): OfflineDataInteractor {
        if (config == DI.Config.RELEASE && offlineDataInteractor == null)
            offlineDataInteractor =
                makeOfflineDataIntercator(
                    getOfflineDataRepository(),
                    downloadListenerLiveData
                )
        return offlineDataInteractor!!
    }

    private fun getOfflineDataRepository(): OfflineDataRepository {
        if (repository == null)
            repository = OfflineDataRepositoryImpl(
                getCloudOfflineDataDataStore(),
                getDiskOfflineDataDataStore()
            )
        return repository!!
    }

    private fun makeOfflineDataIntercator(repository: OfflineDataRepository, downloadListenerLiveData: MutableLiveData<Triple<Float, Float, Boolean>>) =
        object : OfflineDataInteractorImpl(
            repository
        ){
            override fun onDownload(percent: Float, totalSizeMb: Float, done: Boolean) {
                downloadListenerLiveData.postValue(Triple(percent, totalSizeMb, done))
            }
        }

    private fun getCloudOfflineDataDataStore() : CloudOfflineDataDataStore {
        val progressRetrofit = NetworkModule.getProgressRetrofit()
        val dataStore = CloudOfflineDataDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(
                OfflineDataService::class.java,
                progressRetrofit.retrofit
            )
        )
        try {
            return dataStore
        }finally {
            progressRetrofit.setProgressRetrofitListener(dataStore)
        }
    }

    private fun getDiskOfflineDataDataStore() =
        DiskOfflineDataDataStore(LevelsCacheImpl(CacheModule.sharedPreferences), ChaptersCacheImpl(CacheModule.sharedPreferences), ExercisesCacheImpl(CacheModule.sharedPreferences), InterestingCacheImpl(CacheModule.sharedPreferences))
    
}