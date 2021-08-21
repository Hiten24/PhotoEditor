package com.example.clearquoteassignment.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.clearquoteassignment.ui.fragments.isDrawEnable


class DrawableImageView : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)

    private var downX = 0F
    private var downY = 0F
    private var upX = 0F
    private var upY = 0F

    private lateinit var mBitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private lateinit var mMatrix: Matrix

    fun setNewImage(bitmap: Bitmap) {
        mBitmap = Bitmap.createBitmap(bitmap)
        canvas = Canvas(bitmap)
        paint = Paint()
        paint.color = Color.GREEN
        paint.strokeWidth = 10F
        mMatrix = Matrix()
        canvas.drawBitmap(bitmap, 0F, 0F, paint)
        setImageBitmap(bitmap)
    }

    fun clear() {
        canvas.drawBitmap(mBitmap,0F, 0F, paint)
    }

    fun addText(text: String) {
        paint.textSize = 62F
        canvas.drawText(text, 100F, 100F, paint)
    }

    fun addCircle() {
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(mBitmap.width.toFloat()/2, mBitmap.height.toFloat()/2, 200F, paint)
    }

    fun addRect() {
        paint.style = Paint.Style.STROKE
        canvas.drawRect(300F, 200F, 800F, 400F, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isDrawEnable) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = getPointerCoords(event)[0]
                    downY = getPointerCoords(event)[1]
                }
                MotionEvent.ACTION_MOVE -> {
                    upX = getPointerCoords(event)[0]
                    upY = getPointerCoords(event)[1]
                    canvas.drawLine(downX, downY, upX, upY, paint)
                    invalidate()
                    downX = upX
                    downY = upY
                }
                MotionEvent.ACTION_UP -> {
                    upX = getPointerCoords(event)[0]
                    upY = getPointerCoords(event)[1]
                    canvas.drawLine(downX, downY, upX, upY, paint)
                    invalidate()
                }
            }
        }
        return true
    }

    private fun getPointerCoords(e: MotionEvent): FloatArray {
        val index = e.actionIndex
        val cords: FloatArray = floatArrayOf(e.getX(index), e.getY(index))
        val matrix = Matrix()
        imageMatrix.invert(matrix)
        matrix.postTranslate(scrollX.toFloat(), scrollY.toFloat())
        matrix.mapPoints(cords)
        return cords
    }


}