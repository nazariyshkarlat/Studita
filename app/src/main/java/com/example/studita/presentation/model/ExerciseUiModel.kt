package com.example.studita.presentation.model

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseSymbolData
import com.example.studita.domain.entity.exercise.ExerciseType11Filter

sealed class ExerciseUiModel {

    sealed class ExerciseUiModelExercise : ExerciseUiModel() {
        data class ExerciseType1UiModel(
            val title: String,
            val subtitle: String,
            val variants: List<ExerciseImagesRowUiModel>
        ) : ExerciseUiModelExercise()

        data class ExerciseType2UiModel(
            val title: ExerciseImagesRowUiModel,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise()

        data class ExerciseType3UiModel(
            val title: ExerciseSymbolData,
            val subtitle: String,
            val variants: List<ExerciseSymbolData>
        ) : ExerciseUiModelExercise()

        data class ExerciseType4UiModel(
            val title: ExerciseSymbolData,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise()

        data class ExerciseType5And6UiModel(
            val title: String,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise()

        data class ExerciseType7UiModel(val title: String) : ExerciseUiModelExercise()

        data class ExerciseType8And12UiModel(
            val title: String,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise()

        data class ExerciseType9UiModel(val title: String) : ExerciseUiModelExercise()

        data class ExerciseType10UiModel(
            val titleParts: List<String>,
            val subtitle: String,
            val isNumeral: Boolean
        ) : ExerciseUiModelExercise()

        data class ExerciseType11UiModel(
            val titleParts: List<String>,
            val filter: ExerciseType11Filter,
            val compareNumber: String
        ) : ExerciseUiModel.ExerciseUiModelExercise()

        data class ExerciseType13UiModel(
            val exerciseShapeEquationUiModel: List<ExerciseShapeEquationMemberUiModel>,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModel.ExerciseUiModelExercise()

        data class ExerciseType14UiModel(
            val title: ExerciseShapeUiModel,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise()

        data class ExerciseType15UiModel(
            val title: String,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModel.ExerciseUiModelExercise()

        data class ExerciseType16UiModel(
            val titleParts: List<String>,
            val subtitle: String
        ) : ExerciseUiModel.ExerciseUiModelExercise()

        data class ExerciseType17UiModel(
            val exerciseShapeEquationUiModel: List<ExerciseShapeEquationMemberUiModel>,
            val subtitle: String,
            val variants: List<ExerciseShapeUiModel>
        ) : ExerciseUiModel.ExerciseUiModelExercise()

        data class ExerciseType18UiModel(
            val title: String,
            val titleImages: ExerciseImagesRowUiModel
        ) : ExerciseUiModelExercise()

        data class ExerciseType19UiModel(
            val title: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise()

        data class ExerciseType20UiModel(
            val title: ExerciseSymbolData
        ) : ExerciseUiModelExercise()

        data class ExerciseType21UiModel(
            val title: ExerciseSymbolData,
            val variants: List<ExerciseSymbolData>
        ) : ExerciseUiModelExercise()
    }

    sealed class ExerciseUiModelScreen : ExerciseUiModel() {
        data class ScreenType1UiModel(
            val title: ExerciseSymbolData,
            val subtitle: String,
            val imagesRowUiModel: ExerciseImagesRowUiModel
        ) : ExerciseUiModelScreen()

        data class ScreenType2UiModel(val title: String) : ExerciseUiModelScreen()

        data class ScreenType3UiModel(
            val title: String,
            val subtitle: String,
            val partsToInject: List<String>
        ) : ExerciseUiModelScreen()

        data class ScreenType4UiModel(
            val title: String,
            val subtitle: String,
            val image: Drawable
        ) : ExerciseUiModelScreen()

        data class ScreenType5UiModel(
            val title: ExerciseSymbolData,
            val variants: List<String>
        ) : ExerciseUiModelScreen()
    }
}

fun ExerciseData.toUiModel(context: Context) = when (this) {
    is ExerciseData.ExerciseDataExercise.ExerciseType1Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel(
        title,
        subtitle,
        variants.map { it.toUiModel(context) })
    is ExerciseData.ExerciseDataExercise.ExerciseType2Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel(
        title.toUiModel(context),
        subtitle,
        variants.map { it })
    is ExerciseData.ExerciseDataExercise.ExerciseType3Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel(
        title,
        subtitle,
        variants
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType4Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel(
        title,
        subtitle,
        variants
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType5And6Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType5And6UiModel(
        title,
        subtitle,
        variants
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType7Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel(
        title
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType8And12Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8And12UiModel(
        title,
        subtitle,
        variants
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType9Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel(
        title
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType10Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType10UiModel(
        titleParts,
        subtitle,
        isNumeral
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType11Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType11UiModel(
        titleParts,
        filter,
        compareNumber
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType13Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType13UiModel(
        exerciseShapeEquationData.map { it.toUiModel(context) },
        subtitle,
        variants
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType14Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType14UiModel(
        title.toShapeUiModel(context),
        subtitle,
        variants
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType15Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType15UiModel(
        title,
        subtitle,
        variants
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType16Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType16UiModel(
        titleParts,
        subtitle
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType17Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType17UiModel(
        exerciseShapeEquationData.map { it.toUiModel(context) },
        subtitle,
        variants.map { it.toShapeUiModel(context, false) })
    is ExerciseData.ExerciseDataExercise.ExerciseType18Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType18UiModel(
        title,
        titleImages.toUiModel(context)
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType19Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType19UiModel(
        title,
        variants
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType20Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType20UiModel(
        title
    )
    is ExerciseData.ExerciseDataExercise.ExerciseType21Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType21UiModel(
        title,
        variants
    )

    is ExerciseData.ExerciseDataScreen.ScreenType1Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel(
        title,
        subtitle,
        exerciseImagesRowData.toUiModel(context)
    )
    is ExerciseData.ExerciseDataScreen.ScreenType2Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType2UiModel(
        title
    )
    is ExerciseData.ExerciseDataScreen.ScreenType3Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel(
        title,
        subtitle,
        partsToInject
    )
    is ExerciseData.ExerciseDataScreen.ScreenType4Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType4UiModel(
        title,
        subtitle,
        image.getDrawable(context)
    )
    is ExerciseData.ExerciseDataScreen.ScreenType5Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType5UiModel(
        title,
        variants
    )
}