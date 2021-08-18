package com.example.clearquoteassignment

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.clearquoteassignment.databinding.FragmentCameraBinding
import com.example.clearquoteassignment.util.getCameraSelector
import com.example.clearquoteassignment.util.toBitmap

class CameraFragment: Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private var isBackCamOn = true
    private lateinit var imageCapture: ImageCapture

    override fun onStart() {
        super.onStart()
        if(hasPermission()){
            startCamera(getCameraSelector(isBackCamOn))
        }else{
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        binding.ivSwitchCamera.setOnClickListener {
            isBackCamOn = !isBackCamOn
            startCamera(getCameraSelector(isBackCamOn))
        }

        binding.ivCapture.setOnClickListener {
            onClick()
        }

        binding.ivCameraBack.setOnClickListener {
            hideImage()
        }

        return binding.root
    }

    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreviewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview)
            }catch (e: Exception) {
                Log.e("CameraFragment", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun hasPermission() = REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun onClick() {

        imageCapture.takePicture(ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageCapturedCallback() {
            @androidx.camera.core.ExperimentalGetImage
            override fun onCaptureSuccess(image: ImageProxy) {
                showImage()
                binding.ivOutput.setImageBitmap(image.image?.toBitmap())
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, exception.imageCaptureError, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showImage() {
        binding.cameraPreviewView.visibility = View.GONE
        binding.ivOutput.visibility = View.VISIBLE
        binding.ivCameraBack.visibility = View.VISIBLE
    }

    private fun hideImage() {
        binding.cameraPreviewView.visibility = View.VISIBLE
        binding.ivOutput.visibility = View.GONE
        binding.ivCameraBack.visibility = View.GONE
    }
}