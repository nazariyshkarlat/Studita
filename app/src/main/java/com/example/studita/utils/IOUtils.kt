package com.example.studita.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader

object IOUtils {
    fun getRealPathFromURI(contentUri: Uri, context: Context): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(context, contentUri, proj, null, null, null)
        val cursor: Cursor? = loader.loadInBackground()
        cursor?.let {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val result: String = cursor.getString(columnIndex)
            cursor.close()
            return result
        }
        return null
    }
}