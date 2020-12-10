package com.studita.presentation.model

import com.studita.domain.entity.InterestingData
import com.studita.domain.entity.InterestingDataScreen


data class InterestingUiModel(
    val interestingNumber: Int,
    val screens: List<InterestingUiModelScreen>
)

sealed class InterestingUiModelScreen {

    data class InterestingUiModelStartScreen(
        val title: String,
        val subtitle: String,
        val difficultyLevel: Int
    ) : InterestingUiModelScreen()

    data class InterestingUiModelStepScreen(val title: String?, val subtitle: String?) :
        InterestingUiModelScreen()

    object InterestingUiModelSpecificDrumRollScreen : InterestingUiModelScreen()
    data class InterestingUiModelExplanationScreen(val textParts: List<String>) :
        InterestingUiModelScreen()

}

fun InterestingData.toInterestingUiModel() =
    InterestingUiModel(interestingNumber, screens.map { it.toInterestingDataScreenUiModel() })

fun InterestingDataScreen.toInterestingDataScreenUiModel() =
    when (this) {
        is InterestingDataScreen.InterestingDataStartScreen -> InterestingUiModelScreen.InterestingUiModelStartScreen(
            title,
            subtitle,
            difficultyLevel
        )
        is InterestingDataScreen.InterestingDataStepScreen -> InterestingUiModelScreen.InterestingUiModelStepScreen(
            title,
            subtitle
        )
        is InterestingDataScreen.InterestingDataSpecificDrumRollScreen -> InterestingUiModelScreen.InterestingUiModelSpecificDrumRollScreen
        is InterestingDataScreen.InterestingDataExplanationScreen -> InterestingUiModelScreen.InterestingUiModelExplanationScreen(
            textParts
        )
    }