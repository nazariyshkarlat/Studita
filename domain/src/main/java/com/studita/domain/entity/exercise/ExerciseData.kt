package com.studita.domain.entity.exercise

sealed class ExerciseData(open val exerciseNumber: Int?) {
    sealed class ExerciseDataExercise(
        override val exerciseNumber: Int,
        open val isBonus: Boolean,
        open val exerciseAnswer: String?
    ) : ExerciseData(exerciseNumber) {
        data class ExerciseType1Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val subtitle: String,
            val variants: List<ExerciseImagesRowData>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType2Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: ExerciseImagesRowData,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType3Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: ExerciseSymbolData,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType4Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: ExerciseSymbolData,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType5Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType6Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType7Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType8Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType9Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val subtitle: String,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType10Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val titleParts: Pair<String, String>,
            val subtitle: String,
            override val exerciseAnswer: String?, val isNumeral: Boolean
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType11Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val titleParts: List<String>,
            val filter: ExerciseType11Filter,
            val compareNumber: String,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType12Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType13Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val exerciseImagesEquationData: List<ExerciseImagesEquationMemberData>,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType14Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: ExerciseImagesRowData,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType15Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val subtitle: String,
            val variants: List<String>,
            val correctCount: Int,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType16Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val titleParts: List<String>,
            val subtitle: String,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType17Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val exerciseImagesEquationData: List<ExerciseImagesEquationMemberData>,
            val subtitle: String,
            val variants: List<ExerciseImagesRowData>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType18Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val titleImages: ExerciseImagesRowData,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType19Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType20Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: ExerciseSymbolData,
            val titleTextNumber: Int,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType21Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: ExerciseSymbolData,
            val variants: List<ExerciseSymbolData>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType22Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType23Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val titleParts: Pair<String, String>,
            val subtitle: String,
            val variants: Pair<String, String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType24Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType25Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        data class ExerciseType26Data(
            override val exerciseNumber: Int,
            override val isBonus: Boolean,
            val title: String,
            val subtitle: String,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, isBonus, exerciseAnswer)

        fun copy(): ExerciseDataExercise =
            when (this) {
                is ExerciseType1Data -> this.copy(exerciseAnswer = null)
                is ExerciseType2Data -> this.copy(exerciseAnswer = null)
                is ExerciseType3Data -> this.copy(exerciseAnswer = null)
                is ExerciseType4Data -> this.copy(exerciseAnswer = null)
                is ExerciseType5Data -> this.copy(exerciseAnswer = null)
                is ExerciseType6Data -> this.copy(exerciseAnswer = null)
                is ExerciseType7Data -> this.copy(exerciseAnswer = null)
                is ExerciseType8Data -> this.copy(exerciseAnswer = null)
                is ExerciseType9Data -> this.copy(exerciseAnswer = null)
                is ExerciseType10Data -> this.copy(exerciseAnswer = null)
                is ExerciseType11Data -> this.copy(exerciseAnswer = null)
                is ExerciseType12Data -> this.copy(exerciseAnswer = null)
                is ExerciseType13Data -> this.copy(exerciseAnswer = null)
                is ExerciseType14Data -> this.copy(exerciseAnswer = null)
                is ExerciseType15Data -> this.copy(exerciseAnswer = null)
                is ExerciseType16Data -> this.copy(exerciseAnswer = null)
                is ExerciseType17Data -> this.copy(exerciseAnswer = null)
                is ExerciseType18Data -> this.copy(exerciseAnswer = null)
                is ExerciseType19Data -> this.copy(exerciseAnswer = null)
                is ExerciseType20Data -> this.copy(exerciseAnswer = null)
                is ExerciseType21Data -> this.copy(exerciseAnswer = null)
                is ExerciseType22Data -> this.copy(exerciseAnswer = null)
                is ExerciseType23Data -> this.copy(exerciseAnswer = null)
                is ExerciseType24Data -> this.copy(exerciseAnswer = null)
                is ExerciseType25Data -> this.copy(exerciseAnswer = null)
                is ExerciseType26Data -> this.copy(exerciseAnswer = null)
            }

    }

    sealed class ExerciseDataScreen(override val exerciseNumber: Int?) :
        ExerciseData(exerciseNumber) {
        data class ScreenType1Data(
            override val exerciseNumber: Int?,
            val title: ExerciseSymbolData,
            val subtitle: String,
            val exerciseImagesRowData: ExerciseImagesRowData
        ) : ExerciseDataScreen(exerciseNumber)

        data class ScreenType2Data(override val exerciseNumber: Int?, val title: String) :
            ExerciseDataScreen(exerciseNumber)

        data class ScreenType3Data(
            override val exerciseNumber: Int?,
            val title: String, val subtitle: String
        ) : ExerciseDataScreen(exerciseNumber)

        data class ScreenType4Data(
            override val exerciseNumber: Int?,
            val title: String,
            val subtitle: String,
            val image: ImageType,
            val isBonusStart: Boolean,
            val bonusSeconds: Long
        ) : ExerciseDataScreen(exerciseNumber)

        data class ScreenType5Data(
            override val exerciseNumber: Int?,
            val title: ExerciseSymbolData,
            val variants: List<String>
        ) : ExerciseDataScreen(exerciseNumber)
    }

    data class ExerciseExplanationData(override val exerciseNumber: Int?, val textParts: List<String>) : ExerciseData(exerciseNumber)
}

fun List<String>.toExerciseImagesRowData() =
    ExerciseImagesRowData(this[0].toImageType(), this[1].toInt())

data class ExerciseSymbolData(val symbolName: String, val symbol: String)

fun List<String>.toExerciseSymbolData() = ExerciseSymbolData(this[0], this[1])
fun Char.toOperator() = when (this) {
    '+' -> Operator.PLUS
    '-' -> Operator.MINUS
    '*' -> Operator.MULTIPLY
    '/' -> Operator.DIVIDE
    else -> throw UnsupportedOperationException("unknown operator")
}

fun Char.isOperator() = this == Operator.PLUS.toCharacter() ||
        this == Operator.MINUS.toCharacter() ||
        this == Operator.DIVIDE.toCharacter() ||
        this == Operator.MULTIPLY.toCharacter()

fun Operator.toCharacter() = when (this) {
    Operator.PLUS -> '+'
    Operator.MINUS -> '-'
    Operator.MULTIPLY -> '*'
    Operator.DIVIDE -> '/'
}

enum class ExerciseType11Filter {
    BIGGER,
    LOWER
}

enum class ImageType {
    EMOJI,
    ALARM_CLOCK_EMOJI
}

fun String.toImageType() = when (this) {
    "emoji" -> ImageType.EMOJI
    "alarm_clock_emoji" -> ImageType.ALARM_CLOCK_EMOJI
    else -> throw UnsupportedOperationException("unknown image type")
}

enum class Operator {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE
}

sealed class ExerciseImagesEquationMemberData
data class ExerciseOperatorData(val operator: Operator) : ExerciseImagesEquationMemberData()
data class ExerciseImagesRowData(val imageType: ImageType, val count: Int): ExerciseImagesEquationMemberData()
