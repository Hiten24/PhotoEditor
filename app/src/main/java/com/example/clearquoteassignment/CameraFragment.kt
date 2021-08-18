package com.example.clearquoteassignment

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import com.example.clearquoteassignment.databinding.FragmentCameraBinding
import com.example.clearquoteassignment.util.buildPreview
import com.example.clearquoteassignment.util.buildTakePicture
import com.example.clearquoteassignment.util.getCameraSelector
import com.example.clearquoteassignment.util.toBitmap
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraFragment: Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private var isBackCamOn = true
    private lateinit var takePicture: ImageCapture

    private val Context.executor: Executor
        get() = ContextCompat.getMainExecutor(this)

    override fun onStart() {
        super.onStart()
        if(hasPermission()){
            startCamera()
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
            startCamera()
        }

        binding.ivCapture.setOnClickListener {
            clickPicture()
        }

        binding.ivCameraBack.setOnClickListener {
            hideImage()
        }

        return binding.root
    }

    private fun startCamera() {
        lifecycle.coroutineScope.launchWhenResumed {
                bindUseCases(context?.getCameraProvider()!!)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun clickPicture() {
        lifecycle.coroutineScope.launchWhenResumed {
            val imageProxy = takePicture.takePicture(context?.executor!!)
            showImage()
            binding.ivOutput.setImageBitmap(imageProxy.image?.toBitmap())
        }
    }

    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->  
            ProcessCameraProvider.getInstance(this).apply {
                addListener({
                    continuation.resume(get())
                }, executor)
            }
        }

    private fun bindUseCases(cameraProvider: ProcessCameraProvider) {
        val preview = buildPreview(binding.cameraPreviewView.surfaceProvider)
        val cameraSelector = getCameraSelector(isBackCamOn)
        takePicture = buildTakePicture()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, takePicture, preview)
    }

    private suspend fun ImageCapture.takePicture(executor: Executor): ImageProxy {
        return suspendCoroutine { continuation ->  
            takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    continuation.resume(image)
                    image.close()
                    super.onCaptureSuccess(image)
                }

                override fun onError(exception: ImageCaptureException) {
                    continuation.resumeWithException(exception)
                    super.onError(exception)
                }
            })
        }
    }

    private fun hasPermission() = REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
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