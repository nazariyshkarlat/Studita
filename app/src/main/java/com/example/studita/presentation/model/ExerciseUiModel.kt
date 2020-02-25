package com.example.studita.presentation.model

import android.os.Parcel
import android.os.Parcelable

sealed class ExerciseUiModel(open val exerciseNumber: Int): Parcelable{
    data class ExerciseUi1(override val exerciseNumber: Int, val equation: List<String>, val variants: List<String> = listOf("Правда", "Неправда")) : ExerciseUiModel(exerciseNumber), Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.createStringArrayList()!!,
            parcel.createStringArrayList()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(exerciseNumber)
            parcel.writeStringList(equation)
            parcel.writeStringList(variants)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ExerciseUi1> {
            override fun createFromParcel(parcel: Parcel): ExerciseUi1 {
                return ExerciseUi1(parcel)
            }

            override fun newArray(size: Int): Array<ExerciseUi1?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class ExerciseUi2(override val exerciseNumber: Int, val equation: String, val variants: List<String>) : ExerciseUiModel(exerciseNumber), Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.createStringArrayList()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(exerciseNumber)
            parcel.writeString(equation)
            parcel.writeStringList(variants)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ExerciseUi2> {
            override fun createFromParcel(parcel: Parcel): ExerciseUi2 {
                return ExerciseUi2(parcel)
            }

            override fun newArray(size: Int): Array<ExerciseUi2?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class ExerciseUi3(override val exerciseNumber: Int, val equation: String) : ExerciseUiModel(exerciseNumber), Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(exerciseNumber)
            parcel.writeString(equation)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ExerciseUi3> {
            override fun createFromParcel(parcel: Parcel): ExerciseUi3 {
                return ExerciseUi3(parcel)
            }

            override fun newArray(size: Int): Array<ExerciseUi3?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class ExerciseUi4(override val exerciseNumber: Int, val expressionParts: List<String>, val expressionResult: String) : ExerciseUiModel(exerciseNumber), Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.createStringArrayList()!!,
            parcel.readString()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(exerciseNumber)
            parcel.writeStringList(expressionParts)
            parcel.writeString(expressionResult)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ExerciseUi4> {
            override fun createFromParcel(parcel: Parcel): ExerciseUi4 {
                return ExerciseUi4(parcel)
            }

            override fun newArray(size: Int): Array<ExerciseUi4?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class ExerciseUi5(override val exerciseNumber: Int, val expressionParts: List<String>, val expressionResult: String, val variants: List<String>) : ExerciseUiModel(exerciseNumber), Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.createStringArrayList()!!,
            parcel.readString()!!,
            parcel.createStringArrayList()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(exerciseNumber)
            parcel.writeStringList(expressionParts)
            parcel.writeString(expressionResult)
            parcel.writeStringList(variants)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ExerciseUi5> {
            override fun createFromParcel(parcel: Parcel): ExerciseUi5 {
                return ExerciseUi5(parcel)
            }

            override fun newArray(size: Int): Array<ExerciseUi5?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class ExerciseUi6(override val exerciseNumber: Int, val result: String) : ExerciseUiModel(exerciseNumber), Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(exerciseNumber)
            parcel.writeString(result)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ExerciseUi6> {
            override fun createFromParcel(parcel: Parcel): ExerciseUi6 {
                return ExerciseUi6(parcel)
            }

            override fun newArray(size: Int): Array<ExerciseUi6?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class ExerciseUi7(override val exerciseNumber: Int, val expressionParts: List<String>, val expressionResult: String, val variants: List<String>) : ExerciseUiModel(exerciseNumber), Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.createStringArrayList()!!,
            parcel.readString()!!,
            parcel.createStringArrayList()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(exerciseNumber)
            parcel.writeStringList(expressionParts)
            parcel.writeString(expressionResult)
            parcel.writeStringList(variants)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ExerciseUi7> {
            override fun createFromParcel(parcel: Parcel): ExerciseUi7 {
                return ExerciseUi7(parcel)
            }

            override fun newArray(size: Int): Array<ExerciseUi7?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class ExerciseUi8(override val exerciseNumber: Int, val desiredShape: String, val shapes: List<String>) : ExerciseUiModel(exerciseNumber), Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.createStringArrayList()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(exerciseNumber)
            parcel.writeString(desiredShape)
            parcel.writeStringList(shapes)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ExerciseUi8> {
            override fun createFromParcel(parcel: Parcel): ExerciseUi8 {
                return ExerciseUi8(parcel)
            }

            override fun newArray(size: Int): Array<ExerciseUi8?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class ExerciseUi9(override val exerciseNumber: Int, val desiredType: String, val numbers: List<String>) : ExerciseUiModel(exerciseNumber), Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.createStringArrayList()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(exerciseNumber)
            parcel.writeString(desiredType)
            parcel.writeStringList(numbers)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ExerciseUi9> {
            override fun createFromParcel(parcel: Parcel): ExerciseUi9 {
                return ExerciseUi9(parcel)
            }

            override fun newArray(size: Int): Array<ExerciseUi9?> {
                return arrayOfNulls(size)
            }
        }
    }
}