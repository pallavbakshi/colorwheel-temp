package com.pallavbakshi.colorwheel

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.core.graphics.ColorUtils

internal class ColorPointer {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var x = 0f
    private var y = 0f

    var radius = 10f
    private var strokeWidth = 8f
    private var shadowWidth = 10f
    var indicatorColor = Color.WHITE
    private var strokeColor = Color.WHITE
    private var shadowColor = Color.HSVToColor(50, floatArrayOf(204f, 204f, 204f))


    fun setCurrentPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun draw(canvas: Canvas) {
        drawColorIndicator(canvas)
        drawStroke(canvas)
        drawShadow(canvas)
    }

    private fun drawColorIndicator(canvas: Canvas){
        paint.style = Paint.Style.FILL
        paint.color = indicatorColor
        paint.strokeWidth = strokeWidth
        canvas.drawCircle(x, y, radius, paint)
    }

    private fun drawStroke(canvas: Canvas){
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        paint.color = strokeColor
        val strokeRadius = radius - strokeWidth / 2
        canvas.drawCircle(x, y, strokeRadius, paint)
    }

    private fun drawShadow(canvas: Canvas){
        paint.style = Paint.Style.STROKE
        paint.color = shadowColor
        paint.strokeWidth = shadowWidth
        val shadowRadius = radius + strokeWidth / 2
        canvas.drawCircle(x, y, shadowRadius, paint)
    }
}