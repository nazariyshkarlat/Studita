package com.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.R
import com.studita.di.data.ChapterModule
import com.studita.domain.entity.ChapterData
import com.studita.domain.entity.ChapterPartData
import com.studita.domain.interactor.ChapterStatus
import com.studita.presentation.model.ChapterPartUiModel
import com.studita.presentation.model.ChapterUiModel
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.Job

class ChapterViewModel(private val chapterNumber: Int) : ViewModel() {

    val progressState = MutableLiveData<Boolean>()
    val errorEvent= SingleLiveEvent<Boolean>()

    lateinit var chapterData: ChapterData
    private val interactor = ChapterModule.getChapterInteractorImpl()

    private var job: Job? = null

    init {
        getChapter()
    }

    fun getChapter() {
        progressState.value = false
        job = viewModelScope.launchExt(job) {
            when (val status =
                interactor.getChapter(chapterNumber, PrefsUtils.isOfflineModeEnabled())) {
                is ChapterStatus.NoConnection -> errorEvent.value = true
                is ChapterStatus.ServiceUnavailable -> errorEvent.value = false
                is ChapterStatus.Success -> {
                    chapterData = status.result
                    progressState.value = true
                }
            }
        }
    }

    fun isCurrentChapterPart(chapterPartInChapterNumber: Int, chapterNumber: Int) =
        (chapterPartInChapterNumber - 1) == UserUtils.userData.completedParts[chapterNumber - 1]

    fun chapterPartIsOpen(chapterPartInChapterNumber: Int) = chapterPartInChapterNumber - 1 <= UserUtils.userData.completedParts[chapterData.chapterNumber - 1]

    fun chapterPartInChapterNumber(chapterPart: ChapterPartData) = chapterData.parts.indexOfFirst { it.number == chapterPart.number } + 1
}