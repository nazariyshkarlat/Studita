package com.example.studita.presentation.extensions

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Activity.shareImg(view: View) {
    try {
        val bitmap = loadBitmapFromScreen(view)
        bitmap?.let { it1 -> cacheBitmap(it1, view) }
        startActivity(getShareIntent(view))
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

private fun loadBitmapFromScreen(view: View): Bitmap? {
    val viewBitmap = Bitmap.createBitmap(
        view.measuredWidth,
        view.measuredHeight,
        Bitmap.Config.ARGB_8888
    )

    val c = Canvas(viewBitmap)
    view.layout(view.left, view.top, view.right, view.bottom)
    view.draw(c)

    val screenBitmap = Bitmap.createBitmap(viewBitmap.width, viewBitmap.height, viewBitmap.config)

    val canvas = Canvas(screenBitmap)

    canvas.drawColor(getPageBackroundColor(view.context))

    canvas.drawBitmap(viewBitmap, 0F, 0F, null)
    return screenBitmap
}

private fun cacheBitmap(bitmap: Bitmap, view: View){
    val cachePath = File(view.context.cacheDir, "images")
    cachePath.mkdirs() // don't forget to make the directory
    val stream =
        FileOutputStream("$cachePath/image.png") // overwrites this image every time
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    stream.close()
}


private fun getImgUri(view: View) : Uri {
    val imagePath = File(view.context.cacheDir, "images")
    val newFile = File(imagePath, "image.png")
    return FileProvider.getUriForFile(view.context, "com.example.studita.fileprovider", newFile)
}

private fun getShareIntent(view: View) : Intent{
    val contentUri = getImgUri(view)
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    shareIntent.type = view.getAppCompatActivity()?.contentResolver?.getType(contentUri)
    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
    return shareIntent
}