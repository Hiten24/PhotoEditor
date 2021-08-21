package com.example.clearquoteassignment.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.example.clearquoteassignment.R
import com.example.clearquoteassignment.cameraPermissionGranted
import com.example.clearquoteassignment.data.CapturedImage
import com.example.clearquoteassignment.databinding.FragmentCameraBinding
import com.example.clearquoteassignment.permissionLauncher
import com.example.clearquoteassignment.permissionToRequest
import com.example.clearquoteassignment.util.buildPreview
import com.example.clearquoteassignment.util.buildTakePicture
import com.example.clearquoteassignment.util.getCameraSelector
import com.example.clearquoteassignment.util.toBitmap
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraFragment: Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentCameraBinding
    private var isBackCamOn = true
    private lateinit var takePicture: ImageCapture

    private val Context.executor: Executor
        get() = ContextCompat.getMainExecutor(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if(permissionToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionToRequest.toTypedArray())
            startCamera()
        }
        if(cameraPermissionGranted) {
            startCamera()
        }

        binding = FragmentCameraBinding.inflate(inflater, container, false)

        binding.ivSwitchCamera.setOnClickListener(this)
        binding.ivCapture.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.ivSwitchCamera -> {
                isBackCamOn = !isBackCamOn
                startCamera()
            }
            R.id.ivCapture -> clickPicture()
        }
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
            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
            findNavController().navigate(CameraFragmentDirections.actionCameraFragmentToImageFragment(
                CapturedImage(imageProxy.image?.toBitmap())))
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
}