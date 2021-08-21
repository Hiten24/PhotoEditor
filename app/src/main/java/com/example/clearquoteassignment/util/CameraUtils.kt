package com.example.clearquoteassignment.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview


fun buildTakePicture(): ImageCapture = ImageCapture.Builder()
    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
    .build()

fun buildPreview(surfaceProvider: Preview.SurfaceProvider): Preview = Preview.Builder()
    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
    .build()
    .apply {
        setSurfaceProvider(surfaceProvider)
    }

fun getCameraSelector(isBackCam: Boolean): CameraSelector {
    return if(isBackCam) {
        CameraSelector.DEFAULT_BACK_CAMERA
    }else {
        CameraSelector.DEFAULT_FRONT_CAMERA
    }
}

fun Image.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size).rotate()
}

fun Bitmap.rotate(): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(90F)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}