package com.surendramaran.yolov8imageclassification.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.File

object Utils {

//    fun rotatedBitmap(bitmap: Bitmap, rotation: Int) : Bitmap {
//        val exifOrientation = computeExifOrientation(rotation)
//        val matrix = decodeExifOrientation(exifOrientation)
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//    }
//
//    fun fileToBitmap(filePath: String): Bitmap? {
//        return BitmapFactory.decodeFile(filePath)
//    }

    fun createImageFile(context: Context): File {
        val uupDir = File(context.filesDir, "surendramaran.com")
        if (!uupDir.exists()) {
            uupDir.mkdir()
        }
        return File.createTempFile("${System.currentTimeMillis()}", ".jpg", uupDir)
    }

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            if (bitmap.config == Bitmap.Config.HARDWARE) {
                bitmap.copy(Bitmap.Config.ARGB_8888, false)
            } else {
                bitmap
            }
        } catch (e: Exception) {
            Log.e("BitmapError", "Failed to load bitmap from URI", e)
            null
        }
    }

    fun getCameraId(cameraManager: CameraManager): String {
        val cameraIds = cameraManager.cameraIdList
        for (id in cameraIds) {
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                return id
            }
        }
        return ""
    }

//    private fun computeExifOrientation(rotationDegrees: Int) = when (rotationDegrees) {
//        0 -> ExifInterface.ORIENTATION_NORMAL
//        90 -> ExifInterface.ORIENTATION_ROTATE_90
//        180 -> ExifInterface.ORIENTATION_ROTATE_180
//        270 -> ExifInterface.ORIENTATION_ROTATE_270
//        else -> ExifInterface.ORIENTATION_UNDEFINED
//    }
//
//    private fun decodeExifOrientation(exifOrientation: Int): Matrix {
//        val matrix = Matrix()
//
//        when (exifOrientation) {
//            ExifInterface.ORIENTATION_NORMAL -> Unit
//            ExifInterface.ORIENTATION_UNDEFINED -> Unit
//            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
//            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
//            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
//            else -> Log.e("Camera", "Invalid orientation: $exifOrientation")
//        }
//
//        return matrix
//    }

    fun rotateImageIfRequired(context: Context, bitmap: Bitmap, photoUri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(photoUri)
        val exif = inputStream?.let { ExifInterface(it) }

        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap // No rotation needed
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}