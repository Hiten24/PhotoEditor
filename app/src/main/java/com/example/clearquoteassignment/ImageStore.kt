package com.example.clearquoteassignment

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.example.clearquoteassignment.util.sdk29AndUp
import java.io.IOException
import java.lang.Exception

var readPermissionGranted = false
var writePermissionGranted = false
lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

fun Context.updateOrRequestPermission() {
    val hasReadPermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
    val hasWritePermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
    val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    readPermissionGranted = hasReadPermission
    writePermissionGranted = hasWritePermission || minSdk29

    val permissionToRequest = mutableListOf<String>()
    if(!writePermissionGranted) {
        permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    if(!readPermissionGranted) {
        permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    if(permissionToRequest.isNotEmpty()) {
        permissionLauncher.launch(permissionToRequest.toTypedArray())
    }
}

fun Context.savePhotoToExternalStorage(displayName: String, bmp: Bitmap): Boolean {
    val imageCollection = sdk29AndUp {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val contentValue = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.WIDTH, bmp.width)
        put(MediaStore.Images.Media.HEIGHT, bmp.height)
    }

    return try {
        contentResolver.insert(imageCollection, contentValue)?.also { uri ->
            contentResolver.openOutputStream(uri).use { outputStream ->
                if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                    throw IOException("Couldn't save bitmap")
                }
            }
        }?: throw IOException("Couldn't create MediaStore entry")
        true
    }catch (e: Exception) {
        e.printStackTrace()
        false
    }
}