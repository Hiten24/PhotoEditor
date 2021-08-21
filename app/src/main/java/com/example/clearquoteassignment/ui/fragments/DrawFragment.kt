package com.example.clearquoteassignment.ui.fragments

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clearquoteassignment.R
import com.example.clearquoteassignment.databinding.FragmentDrawBinding

var isDrawEnable = false

class DrawFragment: Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentDrawBinding
    private lateinit var image: Bitmap
    private lateinit var drawImage: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrawBinding.inflate(inflater, container, false)

        image = DrawFragmentArgs.fromBundle(requireArguments()).image.image!!
        drawImage = Bitmap.createBitmap(image)
        binding.ivImage.setNewImage(image)

        binding.ivBackBtn.setOnClickListener(this)
        binding.ivDone.setOnClickListener(this)

        binding.ivDraw.setOnClickListener(this)
        binding.ivCircle.setOnClickListener(this)
        binding.ivSquare.setOnClickListener(this)
        binding.ivText.setOnClickListener(this)

        binding.btnTextEntered.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.ivBackBtn -> {
                binding.ivImage.clear()
                activity?.onBackPressed()
            }
            R.id.ivDone -> {
                activity?.onBackPressed()
            }
            R.id.ivDraw -> {
                isDrawEnable = !isDrawEnable
                if(isDrawEnable) {
                    binding.ivDraw.imageTintList = ColorStateList.valueOf(Color.DKGRAY)
                }else {
                    binding.ivDraw.imageTintList = ColorStateList.valueOf(Color.parseColor("#757575"))
                }
            }
            R.id.ivText -> {
                binding.textBackgroundView.visibility = View.VISIBLE
                binding.btnTextEntered.visibility = View.VISIBLE
                binding.etText.visibility = View.VISIBLE
            }
            R.id.ivCircle -> {
                binding.ivImage.addCircle()
            }
            R.id.ivSquare -> {
                binding.ivImage.addRect()
            }
            R.id.btnTextEntered -> {
                binding.textBackgroundView.visibility = View.GONE
                binding.btnTextEntered.visibility = View.GONE
                binding.etText.visibility = View.GONE
                val text = binding.etText.text.toString()
                binding.ivImage.addText(text)
            }
        }
    }
}