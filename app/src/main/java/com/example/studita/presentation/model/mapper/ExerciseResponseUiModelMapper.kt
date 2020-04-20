package com.example.studita.presentation.model.mapper

import android.content.Context
import com.example.studita.R
import com.example.studita.data.entity.exercise.ExerciseResponseDescriptionContent
import com.example.studita.data.entity.exercise.ExerciseResponseDescriptionEntity
import com.example.studita.data.entity.exercise.ExerciseResponseEntity
import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExerciseResponseDescriptionContentData
import com.example.studita.domain.entity.exercise.ExerciseResponseDescriptionData
import com.example.studita.domain.entity.exercise.ExerciseShapeData
import com.example.studita.presentation.model.ExerciseResponseDescriptionContentUiModel
import com.example.studita.presentation.model.ExerciseResponseDescriptionUiModel
import com.example.studita.presentation.model.ExerciseResponseUiModel
import com.example.studita.presentation.model.ExerciseShapeUiModel
import java.io.IOException

class ExerciseResponseUiModelMapper(private val context: Context) : Mapper<ExerciseResponseData, ExerciseResponseUiModel>{
    override fun map(source: ExerciseResponseData): ExerciseResponseUiModel {
        return ExerciseResponseUiModel(
            source.exerciseResult,
            mapDescription(source.description)
        )
    }


    private fun mapDescription(source: ExerciseResponseDescriptionData?): ExerciseResponseDescriptionUiModel? {
        return source?.let {
            ExerciseResponseDescriptionUiModel(
                when (val descriptionContentData = it.descriptionContent) {
                    is ExerciseResponseDescriptionContentData.DescriptionContentArray -> ExerciseResponseDescriptionContentUiModel.DescriptionContentArray(
                        mapShape(descriptionContentData.descriptionContent)
                    )
                    is ExerciseResponseDescriptionContentData.DescriptionContentString -> {
                        mapDescriptionContentString(descriptionContentData)
                    }
                }
            )
        }
    }

    private fun mapShape(exerciseShapeData: ExerciseShapeData): ExerciseShapeUiModel{
        return ExerciseShapeUiModel(
            when(exerciseShapeData.shape){
                "rect"-> context.resources.getDrawable(R.drawable.exercise_rectangle_white, context.theme)
                else -> throw IOException("unknown exercise shape type")
            }, exerciseShapeData.count)
    }

    private fun mapDescriptionContentString(descriptionContentData: ExerciseResponseDescriptionContentData.DescriptionContentString): ExerciseResponseDescriptionContentUiModel.DescriptionContentString =
        ExerciseResponseDescriptionContentUiModel.DescriptionContentString(when (descriptionContentData.descriptionContent) {
            "true" -> context.getString(R.string.true_answer)
            "false" -> context.getString(R.string.false_answer)
            else -> descriptionContentData.descriptionContent
        }
        )
}