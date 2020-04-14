package com.example.studita.presentation.model.mapper

import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.InterestingData
import com.example.studita.domain.entity.InterestingDataScreen
import com.example.studita.presentation.model.InterestingUiModel
import com.example.studita.presentation.model.InterestingUiModelScreen

class InterestingUiModelMapper : Mapper<InterestingData, InterestingUiModel> {
    override fun map(source: InterestingData): InterestingUiModel =
        InterestingUiModel(source.interestingNumber, mapInterestingScreens(source.screens))

    private fun mapInterestingScreens(source: List<InterestingDataScreen>) =
        source.map {
            when(it){
                is InterestingDataScreen.InterestingDataStartScreen -> InterestingUiModelScreen.InterestingUiModelStartScreen(it.title, it.subtitle, it.difficultyLevel)
                is InterestingDataScreen.InterestingDataStepScreen -> InterestingUiModelScreen.InterestingUiModelStepScreen(it.title, it.subtitle)
                is InterestingDataScreen.InterestingDataSpecificDrumRollScreen -> InterestingUiModelScreen.InterestingUiModelSpecificDrumRollScreen
                is InterestingDataScreen.InterestingDataExplanationScreen -> InterestingUiModelScreen.InterestingUiModelExplanationScreen(it.textParts)
            }
        }

}