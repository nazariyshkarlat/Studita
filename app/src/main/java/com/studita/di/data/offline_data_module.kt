package com.studita.di.data

import androidx.lifecycle.MutableLiveData
import com.studita.data.cache.chapter.ChaptersCacheImpl
import com.studita.data.cache.exercises.ExercisesCacheImpl
import com.studita.data.cache.interesting.InterestingCacheImpl
import com.studita.data.cache.levels.LevelsCacheImpl
import com.studita.data.database.StuditaDatabase
import com.studita.data.entity.ProgressRetrofit
import com.studita.data.entity.setProgressRetrofitListener
import com.studita.data.net.OfflineDataService
import com.studita.data.repository.OfflineDataRepositoryImpl
import com.studita.data.repository.datasource.offline_data.CloudOfflineDataDataStore
import com.studita.data.repository.datasource.offline_data.DiskOfflineDataDataStore
import com.studita.di.*
import com.studita.domain.interactor.offline_data.OfflineDataInteractor
import com.studita.domain.interactor.offline_data.OfflineDataInteractorImpl
import com.studita.domain.repository.OfflineDataRepository
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

fun createOfflineDataModule(config: DI.Config) =  configModule(configuration = config){

    single {
            (repository: OfflineDataRepository, downloadListenerLiveData: MutableLiveData<Triple<Float, Float, Boolean>>) ->
        provideOfflineDataInteractor(repository, downloadListenerLiveData)
    }

    single{
        OfflineDataRepositoryImpl(
                get(),
                get()
            )
    } bind (OfflineDataRepository::class)

    single{
        provideCloudOfflineDataDataStore()
    }

    single {
        DiskOfflineDataDataStore(
            LevelsCacheImpl(GlobalContext.get().get()),
            ChaptersCacheImpl(GlobalContext.get().get()),
            ExercisesCacheImpl(GlobalContext.get().get()),
            InterestingCacheImpl(GlobalContext.get().get())
        )
    }
}

private fun provideOfflineDataInteractor(repository: OfflineDataRepository, downloadListenerLiveData: MutableLiveData<Triple<Float, Float, Boolean>>) =
    object : OfflineDataInteractorImpl(repository){
    override fun onDownload(percent: Float, totalSizeMb: Float, done: Boolean) {
        downloadListenerLiveData.postValue(Triple(percent, totalSizeMb, done))
    }
}

private fun provideCloudOfflineDataDataStore(): CloudOfflineDataDataStore{
    val progressRetrofit = get().get<ProgressRetrofit>(named("progress_retrofit"))
    val dataStore = CloudOfflineDataDataStore(
        GlobalContext.get().get(),
        getService(OfflineDataService::class.java, progressRetrofit.retrofit)
    )
    try {
        return dataStore
    }finally {
        progressRetrofit.setProgressRetrofitListener(dataStore)
    }
}