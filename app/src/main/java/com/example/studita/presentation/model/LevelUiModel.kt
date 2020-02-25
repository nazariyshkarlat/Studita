package com.example.studita.presentation.model

import android.os.Parcel
import android.os.Parcelable

sealed class LevelUiModel{

    data class LevelNumber(val value: Int) : LevelUiModel(), Parcelable {
        constructor(parcel: Parcel) : this(parcel.readInt())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(value)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<LevelNumber> {
            override fun createFromParcel(parcel: Parcel): LevelNumber {
                return LevelNumber(parcel)
            }

            override fun newArray(size: Int): Array<LevelNumber?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class LevelChapter(val chapterNumber: Int, val chapterTitle: String, val chapterSubtitle: String, val tasksCount: Int) : LevelUiModel(), Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(chapterNumber)
            parcel.writeString(chapterTitle)
            parcel.writeString(chapterSubtitle)
            parcel.writeInt(tasksCount)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<LevelChapter> {
            override fun createFromParcel(parcel: Parcel): LevelChapter {
                return LevelChapter(parcel)
            }

            override fun newArray(size: Int): Array<LevelChapter?> {
                return arrayOfNulls(size)
            }
        }
    }

}
