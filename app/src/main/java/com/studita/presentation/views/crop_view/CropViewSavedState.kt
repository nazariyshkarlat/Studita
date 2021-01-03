package com.studita.presentation.views.crop_view

import android.graphics.Matrix
import android.os.Parcel
import android.os.Parcelable
import android.view.View

class CropViewSavedState : View.BaseSavedState {
    @JvmField
    public var width = 0f
    @JvmField
    public var height = 0f
    @JvmField
    public var rotation = 0f
    @JvmField
    public var x = 0f
    @JvmField
    public var y = 0f
    @JvmField
    public var scale = 0f
    @JvmField
    public var minimumScale = 0f
    @JvmField
    public var baseRotation = 0f
    @JvmField
    public var orientation = 0f
    @JvmField
    public var matrix: Matrix? = null
    @JvmField
    public var cropWidth = 0f

    internal constructor(superState: Parcelable?) : super(superState) {}
    private constructor(`in`: Parcel) : super(`in`) {
        width = `in`.readFloat()
        height = `in`.readFloat()
        rotation = `in`.readFloat()
        x = `in`.readFloat()
        y = `in`.readFloat()
        scale = `in`.readFloat()
        minimumScale = `in`.readFloat()
        baseRotation = `in`.readFloat()
        orientation = `in`.readFloat()
        val matrixValues = FloatArray(9)
        `in`.readFloatArray(matrixValues)
        matrix = Matrix()
        matrix!!.setValues(matrixValues)
        cropWidth = `in`.readFloat()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeFloat(width)
        out.writeFloat(height)
        out.writeFloat(rotation)
        out.writeFloat(x)
        out.writeFloat(y)
        out.writeFloat(scale)
        out.writeFloat(minimumScale)
        out.writeFloat(baseRotation)
        out.writeFloat(orientation)
        val matrixValues = FloatArray(9)
        matrix!!.getValues(matrixValues)
        out.writeFloatArray(matrixValues)
        out.writeFloat(cropWidth)
    }

    companion object CREATOR : Parcelable.Creator<CropViewSavedState> {
            override fun createFromParcel(`in`: Parcel): CropViewSavedState {
                return CropViewSavedState(`in`)
            }

            override fun newArray(size: Int): Array<CropViewSavedState?> {
                return arrayOfNulls(size)
            }
        }

}