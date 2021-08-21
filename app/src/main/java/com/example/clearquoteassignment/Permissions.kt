package com.example.clearquoteassignment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

var readPermissionGranted = false
var writePermissionGranted = false
var cameraPermissionGranted = false

lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
val permissionToRequest = mutableListOf<String>()

fun Context.updateOrRequestPermission() {
    val hasReadPermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
    val hasWritePermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
    val hasCameraPermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
    val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    readPermissionGranted = hasReadPermission
    writePermissionGranted = hasWritePermission || minSdk29
    cameraPermissionGranted = hasCameraPermission

    if(!writePermissionGranted) {
        permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    if(!readPermissionGranted) {
        permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    if(!cameraPermissionGranted) {
        permissionToRequest.add(Manifest.permission.CAMERA)
    }
}