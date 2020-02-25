package com.example.studita.presentation.model

import android.os.Parcel
import android.os.Parcelable

data class ChapterPartUiModel( val chapterPartNumber: Int, val chapterPartName: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(chapterPartNumber)
        parcel.writeString(chapterPartName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChapterPartUiModel> {
        override fun createFromParcel(parcel: Parcel): ChapterPartUiModel {
            return ChapterPartUiModel(parcel)
        }

        override fun newArray(size: Int): Array<ChapterPartUiModel?> {
            return arrayOfNulls(size)
        }
    }
}