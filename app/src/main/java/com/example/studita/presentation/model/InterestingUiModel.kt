package com.example.studita.presentation.model


data class InterestingUiModel(val interestingNumber: Int, val screens: List<InterestingUiModelScreen>)

sealed class InterestingUiModelScreen{

    data class InterestingUiModelStartScreen(val title: String, val subtitle: String, val difficultyLevel: Int) : InterestingUiModelScreen()

    data class InterestingUiModelStepScreen(val title: String?, val subtitle: String?) : InterestingUiModelScreen()

    object InterestingUiModelSpecificDrumRollScreen : InterestingUiModelScreen()

    data class InterestingUiModelExplanationScreen(val textParts: List<String>) : InterestingUiModelScreen()

}