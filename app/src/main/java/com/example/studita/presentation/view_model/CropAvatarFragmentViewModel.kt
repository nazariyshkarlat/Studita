package com.example.studita.presentation.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.example.studita.utils.IOUtils
import com.example.studita.utils.ImageUtils.rotateIfRequired
import com.example.studita.utils.ScreenUtils
import java.io.IOException

class CropAvatarFragmentViewModel : ViewModel(){

    private var resultBitmap: Bitmap? = null

    fun getBitmap(uri: Uri, context: Context): Bitmap? {
        return if (resultBitmap == null) {
            var bitmap = if (uri.scheme.contains("file"))
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri).rotateIfRequired(context, uri)
            else
                BitmapFactory.decodeFile(IOUtils.getRealPathFromURI(uri, context)).rotateIfRequired(context, uri)

            if (bitmap != null) {
                val scaleFactor = (bitmap.width / ScreenUtils.getScreenWidth().toFloat()).coerceAtMost(bitmap.height / ScreenUtils.getScreenHeight().toFloat())

                if (scaleFactor > 1F)
                    bitmap =  Bitmap.createScaledBitmap(bitmap, (bitmap.width / scaleFactor).toInt(), (bitmap.height / scaleFactor).toInt(), true)
            }
            resultBitmap = bitmap
            bitmap
        } else
            resultBitmap
    }

}