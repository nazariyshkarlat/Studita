package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.ChapterModule
import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.domain.interactor.ChapterStatus
import com.example.studita.presentation.model.ChapterPartUiModel
import com.example.studita.presentation.model.ChapterUiModel
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.Job

class ChapterViewModel : ViewModel() {

    val progressState = MutableLiveData<Boolean>()
    val errorState = SingleLiveEvent<Int>()

    lateinit var chapterData: ChapterData
    private val interactor = ChapterModule.getChapterInteractorImpl()

    private var job: Job? = null

    fun getChapter(chapterNumber: Int) {
        job = viewModelScope.launchExt(job) {
            progressState.postValue(false)
            when (val status =
                interactor.getChapter(chapterNumber, PrefsUtils.isOfflineModeEnabled())) {
                is ChapterStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is ChapterStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is ChapterStatus.Success -> {
                    progressState.postValue(true)
                    chapterData = status.result
                }
            }
        }
    }

    fun isCurrentChapterPart(chapterPartInChapterNumber: Int, chapterNumber: Int) =
        (chapterPartInChapterNumber - 1) == UserUtils.userData.completedParts[chapterNumber - 1]

    fun chapterPartIsOpen(chapterPartInChapterNumber: Int) = chapterPartInChapterNumber - 1 <= UserUtils.userData.completedParts[chapterData.chapterNumber - 1]

    fun chapterPartInChapterNumber(chapterPart: ChapterPartData) = chapterData.parts.indexOfFirst { it.number == chapterPart.number } + 1
}