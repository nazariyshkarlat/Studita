package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.interesting.InterestingEntity
import com.example.studita.data.entity.interesting.InterestingEntityStepEntity
import com.example.studita.domain.entity.InterestingData
import com.example.studita.domain.entity.InterestingDataScreen
import com.example.studita.domain.entity.InterestingDataSpecific
import java.io.IOException

class InterestingDataMapper : Mapper<InterestingEntity, InterestingData>{
    override fun map(source: InterestingEntity): InterestingData =
        InterestingData(source.number, listOf(InterestingDataScreen.InterestingDataStartScreen(source.title, source.subtitle, source.difficultyLevel), InterestingDataScreen.InterestingDataExplanationScreen(source.explanationParts), *mapInterestingSteps(source.steps).toTypedArray()))


    private fun mapInterestingSteps(source: List<InterestingEntityStepEntity>): List<InterestingDataScreen.InterestingDataStepScreen> =
        source.map { InterestingDataScreen.InterestingDataStepScreen(it.title, it.subtitle, mapInterestingSpecific(it.specific)) }

    private fun mapInterestingSpecific(string: String?) =
        when(string){
            "drumroll" -> InterestingDataSpecific.DRUMROLL
            null -> null
            else -> throw IOException("unknown type of interesting specific")
        }

}