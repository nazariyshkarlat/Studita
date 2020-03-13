package com.example.studita.data.entity.mapper.exercise

import com.example.studita.data.entity.exercise.ExerciseResponseDescriptionEntity
import com.example.studita.data.entity.exercise.ExerciseResponseEntity
import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExerciseResponseDescriptionContentData
import com.example.studita.domain.entity.exercise.ExerciseResponseDescriptionData
import com.example.studita.domain.entity.exercise.ExerciseShapeData

class ExerciseResponseDataMapper : Mapper<ExerciseResponseEntity, ExerciseResponseData> {

    override fun map(source: ExerciseResponseEntity): ExerciseResponseData {
        return ExerciseResponseData(
            source.exerciseResult,
            mapDescription(source.description)
        )
    }


    private fun mapDescription(source: ExerciseResponseDescriptionEntity?): ExerciseResponseDescriptionData?{
        return source?.let{ExerciseResponseDescriptionData(when(it.descriptionContent){
            is com.example.studita.data.entity.exercise.ExerciseResponseDescriptionContent.DescriptionContentString -> ExerciseResponseDescriptionContentData.DescriptionContentString(it.descriptionContent.descriptionContent)
            is com.example.studita.data.entity.exercise.ExerciseResponseDescriptionContent.DescriptionContentArray -> ExerciseResponseDescriptionContentData.DescriptionContentArray(
                ExerciseShapeData( it.descriptionContent.descriptionContent[0], it.descriptionContent.descriptionContent[1].toInt()))
        })}
    }
}