package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.interesting.InterestingEntity
import com.example.studita.data.entity.interesting.InterestingEntityStepEntity
import com.example.studita.domain.entity.InterestingData
import com.example.studita.domain.entity.InterestingDataScreen
import java.io.IOException

class InterestingDataMapper : Mapper<InterestingEntity, InterestingData>{
    override fun map(source: InterestingEntity): InterestingData =
        InterestingData(source.number, listOf(InterestingDataScreen.InterestingDataStartScreen(source.title, source.subtitle, source.difficultyLevel), *mapInterestingSteps(source.steps).toTypedArray(), InterestingDataScreen.InterestingDataExplanationScreen(source.explanationParts)))


    private fun mapInterestingSteps(source: List<InterestingEntityStepEntity>): List<InterestingDataScreen> =
        source.map {
            if (it.specific != null)
                mapInterestingSpecific(it.specific)
            else
                InterestingDataScreen.InterestingDataStepScreen(it.title, it.subtitle)
        }

    private fun mapInterestingSpecific(string: String): InterestingDataScreen =
        when(string){
            "drum_roll" -> InterestingDataScreen.InterestingDataSpecificDrumRollScreen
            else -> throw IOException("unknown type of interesting specific")
        }

}