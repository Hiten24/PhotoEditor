package com.example.clearquoteassignment

import android.graphics.Bitmap
import android.media.Image
import android.os.Parcelable
import androidx.camera.core.ImageProxy
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CapturedImage(
    val image: Bitmap?,
): Parcelable
