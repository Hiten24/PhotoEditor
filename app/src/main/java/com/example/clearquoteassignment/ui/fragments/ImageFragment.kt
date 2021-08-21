package com.example.clearquoteassignment.ui.fragments

import android.content.Intent
import android.content.UriMatcher
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.clearquoteassignment.CapturedImage
import com.example.clearquoteassignment.R
import com.example.clearquoteassignment.databinding.FragmentImageBinding
import com.example.clearquoteassignment.savePhotoToExternalStorage
import com.example.clearquoteassignment.writePermissionGranted
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

class ImageFragment: Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentImageBinding
    private lateinit var image: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageBinding.inflate(inflater, container, false)

        image = ImageFragmentArgs.fromBundle(requireArguments()).image.image!!

        binding.ivImage.setImageBitmap(image)

        binding.ivBackBtn.setOnClickListener(this)
        binding.btnDraw.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.ivShareImage.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackBtn -> activity?.onBackPressed()
            R.id.btnDraw -> findNavController().
                navigate(ImageFragmentDirections.actionImageFragmentToDrawFragment(CapturedImage(image)))
            R.id.btnSave -> saveImage(image)
            R.id.ivShareImage -> shareImage(image)
        }
    }

    private fun saveImage(image: Bitmap) {
        if(writePermissionGranted) {
            context?.savePhotoToExternalStorage(UUID.randomUUID().toString(), image)
        }
    }

    private fun shareImage(image: Bitmap) {

        val uri = saveCache(image)
        val intent = Intent(android.content.Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(Intent.createChooser(intent, "Choose app to share Image"))
    }

    private fun saveCache(image: Bitmap) : Uri? {
        var uri: Uri? = null
        val imageFolder = File(context?.cacheDir, "images")
        try {
            imageFolder.mkdir()
            val file = File(imageFolder, "shared_image.jpg")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(requireContext(), "com.example.clearquoteassignment.fileProvider", file)
        }catch (e: IOException) {
            Log.d("ImageFragment", "IOException while trying to write file for sharing: " + e.message)
        }
        return uri
    }
}