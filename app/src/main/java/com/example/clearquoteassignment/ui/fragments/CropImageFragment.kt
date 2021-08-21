package com.example.clearquoteassignment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.clearquoteassignment.data.CapturedImage
import com.example.clearquoteassignment.databinding.FragmentCropImageBinding

class CropImageFragment: Fragment() {

    private lateinit var binding: FragmentCropImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCropImageBinding.inflate(inflater, container, false)

        val image = CropImageFragmentArgs.fromBundle(requireArguments()).image.image

        binding.cropImageView.setImageBitmap(image)

        binding.ivDone.setOnClickListener {
            val croppedImage = binding.cropImageView.croppedImage
            findNavController().navigate(CropImageFragmentDirections.actionCropImageFragmentToImageFragment(
                CapturedImage(croppedImage)))
        }

        binding.ivCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        return binding.root
    }
}