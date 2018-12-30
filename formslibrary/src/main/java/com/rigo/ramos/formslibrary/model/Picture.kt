package com.rigo.ramos.formslibrary.model

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Environment
import android.util.Log
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Picture(bitmap: Bitmap?=null){


    var data: String? = null
    var contentType: String? = null
    var filename: String? = null
    var bitmap: Bitmap? = null
    var file: File? = null
    var path: String? = null



    init {
        if(bitmap != null) {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            this.filename = "IMG_$timeStamp.jpg"
            this.bitmap = bitmap
        }
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun createImageFile() {
        // Create an image file name
        val storageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera")
        storageDir.mkdirs()
        file = File(storageDir.path + File.separator + "image.jpg")

        file!!.createNewFile()

        // Save a file: path for use with ACTION_VIEW intents
        path = file!!.getAbsolutePath()


    }



    private fun compressImage() {
        if (data == null) {
            bitmap = getResizedBitmap(bitmap!!, 500)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            this.data = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
            this.contentType = "image/jpg"

            Log.e("Bitmap size33 ", "heig : " + bitmap!!.height + " wid: " + bitmap!!.width + "size" + bitmap!!.byteCount)
        }
    }

    fun getValue(): JSONObject{
        compressImage()
        val value = JSONObject()

        value.put("data",data)
        value.put("content_type",contentType)
        value.put("name",filename)
        return value

    }


   companion object {
        fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> return bitmap
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    matrix.setRotate(180f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.setRotate(90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.setRotate(-90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
                else -> return bitmap
            }
            try {
                val bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                bitmap.recycle()
                return bmRotated
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                return null
            }

        }
    }


}