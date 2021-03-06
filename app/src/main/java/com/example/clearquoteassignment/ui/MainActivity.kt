package com.example.clearquoteassignment.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.clearquoteassignment.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                readPermissionGranted =
                    permission[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
                writePermissionGranted =
                    permission[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted
                cameraPermissionGranted =
                    permission[Manifest.permission.CAMERA] ?: cameraPermissionGranted
            }

        updateOrRequestPermission()

    }
}