package com.example.clearquoteassignment

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.clearquoteassignment.databinding.FragmentImageBinding
import com.example.clearquoteassignment.util.toBitmap

class ImageFragment: Fragment() {

    private lateinit var binding: FragmentImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageBinding.inflate(inflater, container, false)

        val args = ImageFragmentArgs.fromBundle(requireArguments())

        binding.ivImage.setImageBitmap(args.image.image)

        binding.ivBackBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        return binding.root
    }
}