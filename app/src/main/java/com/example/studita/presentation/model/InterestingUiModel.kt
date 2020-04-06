package com.example.studita.presentation.model

import com.example.studita.domain.entity.InterestingDataSpecific

data class InterestingUiModel(val interestingNumber: Int, val screens: List<InterestingUiModelScreen>)

sealed class InterestingUiModelScreen{

    data class InterestingUiModelStartScreen(val title: String, val subtitle: String, val difficultyLevel: Int) : InterestingUiModelScreen()

    data class InterestingUiModelStepScreen(val title: String?, val subtitle: String?, val specific: InterestingDataSpecific?) : InterestingUiModelScreen()

    data class InterestingUiModelExplanationScreen(val textParts: List<String>) : InterestingUiModelScreen()

}