package com.pallavbakshi.colorwheel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.ColorUtils
import java.lang.Math.*

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

internal val HUE_COLORS = intArrayOf(
    Color.RED,
    Color.MAGENTA,
    Color.BLUE,
    Color.CYAN,
    Color.GREEN,
    Color.YELLOW,
    Color.RED
)

internal val SATURATION_COLORS = intArrayOf(
    Color.WHITE,
    ColorUtils.setAlphaComponent(Color.WHITE, 0)
)

internal const val HUE_INDEX = 0
internal const val SATURATION_INDEX = 1
internal const val VALUE_INDEX = 2

class ColorWheel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val hueGradient = GradientDrawable().apply {
        gradientType = GradientDrawable.SWEEP_GRADIENT
        shape = GradientDrawable.OVAL
        colors = HUE_COLORS
    }
    private val saturationGradient = GradientDrawable().apply {
        gradientType = GradientDrawable.RADIAL_GRADIENT
        shape = GradientDrawable.OVAL
        colors = SATURATION_COLORS
    }
    private val pointer = ColorPointer()

    var colorChangeListener: ((Int) -> Unit)? = null

    private var centreX = 0
    private var centreY = 0
    private var radius = 0
    private var hsv = floatArrayOf(0f, 0f, 1f)

    private var pointerRadius
        get() = pointer.radius
        set(value) {
            pointer.radius = value
            invalidate()
        }

    init {
        parseAttributes(context, attrs)
    }

    fun setHsv(arr: FloatArray){
        hsv[HUE_INDEX] = arr[0]
        hsv[SATURATION_INDEX] = arr[1]
        hsv[VALUE_INDEX] = arr[2]
        invalidate()
    }

    private fun parseAttributes(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.ColorWheel, 0, R.style.ColorWheelDefaultStyle).apply {
            pointerRadius = getDimensionPixelSize(R.styleable.ColorWheel_pointerRadius, 0).toFloat()
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minDimension = minOf(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )

        setMeasuredDimension(
            resolveSize(minDimension, widthMeasureSpec),
            resolveSize(minDimension, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas){
        drawColorWheel(canvas)
        drawPointer(canvas)
    }

    private fun drawColorWheel(canvas: Canvas){
        val w = width - paddingLeft - paddingRight
        val h = height - paddingTop - paddingBottom

        centreX = paddingLeft + w / 2
        centreY = paddingTop + h / 2
        radius = minOf(w, h) / 2

        val left = centreX - radius
        val top = centreY - radius
        val right = centreX + radius
        val bottom = centreY + radius

        hueGradient.setBounds(left, top, right, bottom)
        saturationGradient.setBounds(left, top, right, bottom)
        saturationGradient.gradientRadius = radius.toFloat()

        hueGradient.draw(canvas)
        saturationGradient.draw(canvas)
    }

    private fun drawPointer(canvas: Canvas){
        var r = hsv[SATURATION_INDEX] * radius
        var theta = toRadians(hsv[HUE_INDEX].toDouble())

        val x = r * cos(theta) + centreX
        val y = -r * sin(theta) + centreY

        pointer.indicatorColor = Color.HSVToColor(hsv)
        pointer.setCurrentPosition(x.toFloat(), y.toFloat())
        pointer.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE,
            MotionEvent.ACTION_UP -> {
                updateColorOnMotionEvent(event)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun updateColorOnMotionEvent(event: MotionEvent) {
        var newHsv = calculateColor(event)
        fireColorListener(Color.HSVToColor(newHsv))
        invalidate()
    }

    private fun calculateColor(event: MotionEvent): FloatArray {
        val legX = event.x - centreX
        val legY = event.y - centreY
        val r = calculateRadius(legX, legY)
        val angle = atan2(-legY, legX)
        val hue = (toDegrees(angle.toDouble()) + 360) % 360
        val saturation = r / radius

        hsv[HUE_INDEX] = hue.toFloat()
        hsv[SATURATION_INDEX] = saturation
        return hsv
    }

    private fun calculateRadius(legX: Float, legY: Float): Float {
        val localRadius = hypot(legX, legY)
        return if (localRadius > radius) radius.toFloat() else localRadius
    }

    private fun fireColorListener(color: Int) {
        colorChangeListener?.invoke(color)
    }



}