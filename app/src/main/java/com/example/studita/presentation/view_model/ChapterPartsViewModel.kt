package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.ChapterPartsModule
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.domain.interactor.ChapterPartsStatus
import com.example.studita.presentation.extensions.launchExt
import com.example.studita.presentation.model.ChapterPartUiModel
import com.example.studita.presentation.model.ChapterUiModel
import com.example.studita.presentation.model.LevelUiModel
import com.example.studita.presentation.model.mapper.ChapterPartUiModelMapper
import kotlinx.coroutines.Job

class ChapterPartsViewModel : ViewModel(){

    val progressState = SingleLiveEvent<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    private val chapterPartUiModelMapper = ChapterPartUiModelMapper()

    lateinit var results: ChapterUiModel
    private val interactor = ChapterPartsModule.getChapterPartsInteractorImpl()

    private var job: Job? = null

    fun getChapterParts(chapterNumber: Int){
        job = viewModelScope.launchExt(job){
            progressState.postValue(false)
            when(val status = interactor.getChapterParts(chapterNumber)){
                is ChapterPartsStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is ChapterPartsStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is ChapterPartsStatus.Success -> {
                    progressState.postValue(true)
                    results = chapterPartUiModelMapper.map(status.result)
                }
            }
        }
    }

}