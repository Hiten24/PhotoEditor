package com.example.clearquoteassignment

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CapturedImage(
    val image: Bitmap?
): Parcelable
