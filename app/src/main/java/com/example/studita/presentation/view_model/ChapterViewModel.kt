package com.example.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.ChapterModule
import com.example.studita.domain.interactor.ChapterStatus
import com.example.studita.presentation.model.ChapterUiModel
import com.example.studita.presentation.utils.launchExt
import com.example.studita.presentation.model.mapper.ChapterUiModelMapper
import kotlinx.coroutines.Job

class ChapterViewModel : ViewModel(){

    val progressState = SingleLiveEvent<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    private val chapterPartUiModelMapper = ChapterUiModelMapper()

    lateinit var results: ChapterUiModel
    private val interactor = ChapterModule.getChapterInteractorImpl()

    private var job: Job? = null

    fun getChapter(chapterNumber: Int){
        job = viewModelScope.launchExt(job){
            progressState.postValue(false)
            when(val status = interactor.getChapter(chapterNumber)){
                is ChapterStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is ChapterStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is ChapterStatus.Success -> {
                    progressState.postValue(true)
                    results = chapterPartUiModelMapper.map(status.result)
                }
            }
        }
    }

}