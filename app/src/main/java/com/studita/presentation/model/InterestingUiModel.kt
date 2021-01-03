package com.studita.presentation.model

import com.studita.domain.entity.InterestingData
import com.studita.domain.entity.InterestingDataScreen
import kotlinx.serialization.Serializable


@Serializable
data class InterestingUiModel(
    val interestingNumber: Int,
    val screens: List<InterestingUiModelScreen>
)

@Serializable
sealed class InterestingUiModelScreen {

    @Serializable
    data class InterestingUiModelStartScreen(
        val title: String,
        val subtitle: String,
        val difficultyLevel: Int
    ) : InterestingUiModelScreen()

    @Serializable
    data class InterestingUiModelStepScreen(val title: String?, val subtitle: String?) :
        InterestingUiModelScreen()

    @Serializable
    object InterestingUiModelSpecificDrumRollScreen : InterestingUiModelScreen()

    @Serializable
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