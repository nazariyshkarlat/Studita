package com.studita.utils

import android.R.array
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Environment
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.FileProvider
import androidx.core.graphics.get
import androidx.exifinterface.media.ExifInterface
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable
import com.studita.utils.ImageUtils.toBooleanArray
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


object ImageUtils {
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

        val c1 = Canvas(viewBitmap)
        view.layout(view.left, view.top, view.right, view.bottom)
        view.draw(c1)

        val screenBitmap =
            Bitmap.createBitmap(viewBitmap.width, viewBitmap.height, viewBitmap.config)

        val c2 = Canvas(screenBitmap)

        c2.drawColor(ThemeUtils.getPageBackgroundColor(view.context))

        c2.drawBitmap(viewBitmap, 0F, 0F, null)
        return screenBitmap
    }

    private fun cacheBitmap(bitmap: Bitmap, view: View) {
        val cachePath = File(view.context.cacheDir, "images")
        cachePath.mkdirs() // don't forget to make the directory
        val stream =
            FileOutputStream("$cachePath/image.png") // overwrites this image every time
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
    }


    private fun getImgUri(view: View): Uri {
        val imagePath = File(view.context.cacheDir, "images")
        val newFile = File(imagePath, "image.png")
        return FileProvider.getUriForFile(view.context, "com.studita.fileprovider", newFile)
    }

    private fun getShareIntent(view: View): Intent {
        val contentUri = getImgUri(view)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.type = view.getAppCompatActivity()?.contentResolver?.getType(contentUri)
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        return shareIntent
    }

    fun Bitmap.rotate(angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            this, 0, 0, this.width, this.height,
            matrix, true
        )
    }

    fun Bitmap.rotateIfRequired(context: Context, path: Uri): Bitmap? {
        val input: InputStream? = context.contentResolver.openInputStream(path)

        if(input != null && path.path != null) {
            val ei = ExifInterface(input)
            val orientation: Int = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> this.rotate(90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> this.rotate(180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> this.rotate(270F)
                ExifInterface.ORIENTATION_NORMAL -> this
                else -> this
            }
        }else{
            return this
        }
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    fun Bitmap.compress(): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = calculateInSampleSize(options, 300, 300)

        options.inJustDecodeBounds = false
        val bos = ByteArrayOutputStream()
        this.compress(CompressFormat.PNG, 0, bos)
        val bitmapdata = bos.toByteArray()
        val bs = ByteArrayInputStream(bitmapdata)
        val img = BitmapFactory.decodeStream(bs, null, options)
        return img
    }


    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).
            val totalPixels = width * height.toFloat()

            // Anything more than 2x the requested pixels we'll sample down further
            val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }
        return inSampleSize
    }

    fun Drawable.getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap
    }

    fun Bitmap.toBooleanArray(): Array<Array<Boolean>>{
        val booleanArray = Array(this.height){Array(this.width){false} }
        for(x: Int in 0 until width){
            for(y: Int in 0 until height){
                if(this[x, y] != Color.TRANSPARENT)
                    booleanArray[y][x] = true
            }
        }
        return booleanArray
    }

    @ColorInt
    fun Bitmap.getOrNull(x: Int, y: Int): Int?{
        return if((y < 0 || x < 0)  || (height <= y || width <= x)) null
        else this[x, y]
    }

    fun Array<Array<Boolean>>.toBitmap(@ColorInt strokeColor: Int): Bitmap{
        val bitmap = Bitmap.createBitmap(this[0].size, this.size, Bitmap.Config.ARGB_8888)
        val intArray = IntArray(this.size*this[0].size)
        for(y: Int in this.indices){
            for(x: Int in this[y].indices){
                intArray[x+y*this[y].size] = if(this[y][x]) strokeColor else Color.TRANSPARENT
            }
        }
        bitmap.setPixels(intArray, 0, this[0].size, 0, 0, this[0].size, this.size)
        return bitmap
    }

    val Bitmap.lastXIndex get() = width-1
    val Bitmap.lastYIndex get() = height-1
}