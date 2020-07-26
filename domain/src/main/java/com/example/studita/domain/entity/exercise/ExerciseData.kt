package com.example.studita.domain.entity.exercise

import java.lang.UnsupportedOperationException

sealed class ExerciseData(open val exerciseNumber: Int?){
    sealed class ExerciseDataExercise(override val exerciseNumber: Int, open val exerciseAnswer: String?): ExerciseData(exerciseNumber) {
        data class ExerciseType1Data(
            override val exerciseNumber: Int,
            val title: String,
            val subtitle: String,
            val variants: List<ExerciseShapeData>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType2And14Data(
            override val exerciseNumber: Int,
            val title: ExerciseShapeData,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType3Data(
            override val exerciseNumber: Int,
            val title: ExerciseSymbolData,
            val subtitle: String,
            val variants: List<ExerciseSymbolData>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType4Data(
            override val exerciseNumber: Int,
            val title: ExerciseSymbolData,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType5And6Data(
            override val exerciseNumber: Int,
            val title: String,
            val subtitle: String,
            val variants: List<String>,
            override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType7Data(override val exerciseNumber: Int,
                                                 val title: String, override val exerciseAnswer: String?) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType8And12Data(override val exerciseNumber: Int,
                                          val title: String, val subtitle: String, val variants: List<String>, override val exerciseAnswer: String?
        ) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType9Data(override val exerciseNumber: Int, val title: String, override val exerciseAnswer: String?) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType10Data(override val exerciseNumber: Int, val titleParts: List<String>, val subtitle: String, override val exerciseAnswer: String?, val isNumeral: Boolean) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType11Data(override val exerciseNumber: Int, val titleParts: List<String>, val filter: ExerciseType11Filter, val compareNumber: String, override val exerciseAnswer: String?) :  ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType13Data(override val exerciseNumber: Int, val exerciseShapeEquationData: List<ExerciseShapeEquationMemberData>, val subtitle: String, val variants: List<String>, override val exerciseAnswer: String?) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType15Data(override val exerciseNumber: Int, val title: String, val subtitle: String, val variants: List<String>, override val exerciseAnswer: String?) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType16Data(override val exerciseNumber: Int, val titleParts: List<String>, val subtitle: String, override val exerciseAnswer: String?) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        data class ExerciseType17Data(override val exerciseNumber: Int, val exerciseShapeEquationData: List<ExerciseShapeEquationMemberData>, val subtitle: String, val variants: List<ExerciseShapeData>, override val exerciseAnswer: String?) : ExerciseDataExercise(exerciseNumber, exerciseAnswer)

        fun copy(): ExerciseDataExercise =
            when(this) {
                is ExerciseType1Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType2And14Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType3Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType4Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType5And6Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType7Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType8And12Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType9Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType10Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType11Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType13Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType15Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType16Data -> this.copy(exerciseAnswer =  null)
                is ExerciseType17Data -> this.copy(exerciseAnswer =  null)
            }

    }
    sealed class ExerciseDataScreen(override val exerciseNumber: Int?): ExerciseData(exerciseNumber) {
        data class ScreenType1Data(
            override val exerciseNumber: Int?,
            val title: String, val subtitle: String, val partsToInject: List<String>, val image: String
        ) : ExerciseDataScreen(exerciseNumber)

        data class ScreenType2Data(override val exerciseNumber: Int?, val title: String) : ExerciseDataScreen(exerciseNumber)

        data class ScreenType3Data(
            override val exerciseNumber: Int?,
            val title: String, val subtitle: String, val partsToInject: List<String>
        ) : ExerciseDataScreen(exerciseNumber)
    }
}

fun List<String>.toExerciseShapeData() = ExerciseShapeData(this[0], this[1].toInt())

data class ExerciseSymbolData(val characterName: String, val symbol: String)

fun List<String>.toExerciseCharacterData() = ExerciseSymbolData(this[0], this[1])
fun Char.toOperator() = when(this){
    '+' -> Operator.PLUS
    '-' -> Operator.MINUS
    '*' -> Operator.MULTIPLY
    '/' -> Operator.DIVIDE
    else -> throw UnsupportedOperationException("unknown operator")
}

fun Operator.toCharacter() = when(this){
    Operator.PLUS -> '+'
    Operator.MINUS -> '-'
    Operator.MULTIPLY -> '*'
    Operator.DIVIDE -> '/'
}

enum class ExerciseType11Filter{
    BIGGER,
    LOWER
}

enum class Operator{
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE
}

sealed class ExerciseShapeEquationMemberData
data class ExerciseOperatorData(val operator: Operator): ExerciseShapeEquationMemberData()
data class ExerciseShapeData(val shape: String, val count: Int): ExerciseShapeEquationMemberData()