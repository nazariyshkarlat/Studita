package com.example.studita.di.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.studita.data.cache.chapter.ChaptersCacheImpl
import com.example.studita.data.cache.exercises.ExercisesCacheImpl
import com.example.studita.data.cache.interesting.InterestingCacheImpl
import com.example.studita.data.cache.levels.LevelsCacheImpl
import com.example.studita.data.net.OfflineDataService
import com.example.studita.data.repository.OfflineDataRepositoryImpl
import com.example.studita.data.repository.datasource.offline_data.CloudOfflineDataDataStore
import com.example.studita.data.repository.datasource.offline_data.DiskOfflineDataDataStore
import com.example.studita.di.CacheModule
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.di.NetworkModule.setProgressRetrofitListener
import com.example.studita.domain.interactor.offline_data.OfflineDataInteractor
import com.example.studita.domain.interactor.offline_data.OfflineDataInteractorImpl
import com.example.studita.domain.repository.OfflineDataRepository

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