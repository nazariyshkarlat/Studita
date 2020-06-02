package com.example.studita.data.entity.mapper

import android.content.Context
import android.graphics.Bitmap
import java.io.*

class BitmapToFileMapper(private val context: Context) : Mapper<Bitmap, File>{

    override fun map(source: Bitmap): File {
        val f = File(context.cacheDir, "avatar")
        f.createNewFile()

        val bos = ByteArrayOutputStream()
        source.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapData = bos.toByteArray()

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(f)
        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        }
        try {
            fos?.let {
                fos.write(bitmapData)
                fos.flush()
                fos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return f
    }

}