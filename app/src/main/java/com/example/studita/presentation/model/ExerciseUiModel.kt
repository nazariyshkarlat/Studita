package com.example.studita.presentation.model

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.studita.domain.entity.exercise.*

sealed class ExerciseUiModel(open val exerciseNumber: Int?){

    sealed class ExerciseUiModelExercise(override val exerciseNumber: Int?) : ExerciseUiModel(exerciseNumber) {
        data class ExerciseType1UiModel(
            override val exerciseNumber: Int?,
            val title: String,
            val subtitle: String,
            val variants: List<ExerciseImagesRowUiModel>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType2UiModel(
            override val exerciseNumber: Int?,
            val title: ExerciseImagesRowUiModel,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType3UiModel(
            override val exerciseNumber: Int?,
            val title: ExerciseSymbolData,
            val subtitle: String,
            val variants: List<ExerciseSymbolData>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType4UiModel(
            override val exerciseNumber: Int?,
            val title: ExerciseSymbolData,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType5And6UiModel(
            override val exerciseNumber: Int?,
            val title: String,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType7UiModel(override val exerciseNumber: Int?,
                                        val title: String) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType8And12UiModel(override val exerciseNumber: Int?,
                                             val title: String,
                                             val subtitle: String,
                                             val variants: List<String>
        ) :ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType9UiModel(override val exerciseNumber: Int?,
                                        val title: String) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType10UiModel(override val exerciseNumber: Int?,
                                         val titleParts: List<String>,
                                         val subtitle: String,
                                         val isNumeral: Boolean) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType11UiModel(override val exerciseNumber: Int,
                                         val titleParts: List<String>,
                                         val filter: ExerciseType11Filter,
                                         val compareNumber: String) :  ExerciseUiModel.ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType13UiModel(override val exerciseNumber: Int,
                                         val exerciseShapeEquationUiModel: List<ExerciseShapeEquationMemberUiModel>,
                                         val subtitle: String,
                                         val variants: List<String>) : ExerciseUiModel.ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType14UiModel(
            override val exerciseNumber: Int?,
            val title: ExerciseShapeUiModel,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType15UiModel(override val exerciseNumber: Int,
                                         val title: String,
                                         val subtitle: String,
                                         val variants: List<String>) : ExerciseUiModel.ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType16UiModel(override val exerciseNumber: Int,
                                         val titleParts: List<String>,
                                         val subtitle: String) : ExerciseUiModel.ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType17UiModel(override val exerciseNumber: Int,
                                         val exerciseShapeEquationUiModel: List<ExerciseShapeEquationMemberUiModel>,
                                         val subtitle: String,
                                         val variants: List<ExerciseShapeUiModel>) : ExerciseUiModel.ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType18UiModel(override val exerciseNumber: Int,
                                      val title: String,
                                         val titleImages: ExerciseImagesRowUiModel) : ExerciseUiModelExercise(exerciseNumber)
    }

    sealed class ExerciseUiModelScreen(override val exerciseNumber: Int?) : ExerciseUiModel(exerciseNumber) {
        data class ScreenType1UiModel(
            override val exerciseNumber: Int?,
            val title: ExerciseSymbolData,
            val subtitle: String,
            val imagesRowUiModel: ExerciseImagesRowUiModel
        ) : ExerciseUiModelScreen(exerciseNumber)

        data class ScreenType2UiModel(override val exerciseNumber: Int?, val title: String) : ExerciseUiModelScreen(exerciseNumber)

        data class ScreenType3UiModel(
            override val exerciseNumber: Int?,
            val title: String, val subtitle: String, val partsToInject: List<String>
        ) : ExerciseUiModelScreen(exerciseNumber)

        data class ScreenType4UiModel(
            override val exerciseNumber: Int?,
            val title: String,
            val subtitle: String,
            val image: Drawable
        ) : ExerciseUiModelScreen(exerciseNumber)
    }
}

fun ExerciseData.toUiModel(context: Context) = when (this) {
    is ExerciseData.ExerciseDataExercise.ExerciseType1Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel(exerciseNumber, title, subtitle, variants.map { it.toUiModel(context) })
    is ExerciseData.ExerciseDataExercise.ExerciseType2Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel(exerciseNumber, title.toUiModel(context), subtitle, variants.map{it.toString()})
    is ExerciseData.ExerciseDataExercise.ExerciseType3Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel(exerciseNumber, title, subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType4Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel(exerciseNumber, title, subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType5And6Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType5And6UiModel(exerciseNumber, title, subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType7Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel(exerciseNumber,title)
    is ExerciseData.ExerciseDataExercise.ExerciseType8And12Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8And12UiModel(exerciseNumber, title, subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType9Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel(exerciseNumber,title)
    is ExerciseData.ExerciseDataExercise.ExerciseType10Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType10UiModel(exerciseNumber,titleParts, subtitle, isNumeral)
    is ExerciseData.ExerciseDataExercise.ExerciseType11Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType11UiModel(exerciseNumber,titleParts, filter, compareNumber)
    is ExerciseData.ExerciseDataExercise.ExerciseType13Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType13UiModel(exerciseNumber, exerciseShapeEquationData.map { it.toUiModel(context) }, subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType14Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType14UiModel(exerciseNumber, title.toShapeUiModel(context), subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType15Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType15UiModel(exerciseNumber, title, subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType16Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType16UiModel(exerciseNumber, titleParts, subtitle)
    is ExerciseData.ExerciseDataExercise.ExerciseType17Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType17UiModel(exerciseNumber, exerciseShapeEquationData.map { it.toUiModel(context) }, subtitle, variants.map { it.toShapeUiModel(context, false) })
    is ExerciseData.ExerciseDataExercise.ExerciseType18Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType18UiModel(exerciseNumber, title, titleImages.toUiModel(context))

    is ExerciseData.ExerciseDataScreen.ScreenType1Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel(exerciseNumber, title, subtitle, exerciseImagesRowData.toUiModel(context))
    is ExerciseData.ExerciseDataScreen.ScreenType2Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType2UiModel(exerciseNumber, title)
    is ExerciseData.ExerciseDataScreen.ScreenType3Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel(exerciseNumber, title, subtitle, partsToInject)
    is ExerciseData.ExerciseDataScreen.ScreenType4Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType4UiModel(exerciseNumber, title, subtitle, image.getDrawable(context))
}