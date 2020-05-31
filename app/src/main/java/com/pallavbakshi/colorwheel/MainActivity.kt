package com.pallavbakshi.colorwheel

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar

class MainActivity : AppCompatActivity() {

    private lateinit var colorWheel: ColorWheel
    private lateinit var colorGroup: RadioGroup
    private lateinit var currentTile: RadioButton
    private lateinit var colorBrightness: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colorWheel = findViewById(R.id.color_wheel)
        colorGroup = findViewById(R.id.color_group)
        colorBrightness = findViewById(R.id.brightness)
        currentTile = findViewById(colorGroup.checkedRadioButtonId)
        attachListener(colorWheel, colorBrightness)
    }

    private fun attachListener(colorWheel: ColorWheel, colorBrightness: SeekBar){
        colorWheel.colorChangeListener = this::onColorWheelUpdateListener
        colorBrightness.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var progress = seekBar?.progress
                if (progress != null) {
                    var hsv = colorToHSV(currentTile.currentTextColor)
                    hsv[VALUE_INDEX] = progress / 100f
                    currentTile.setTextColor(Color.HSVToColor(hsv))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }

    private fun onColorWheelUpdateListener(rgb: Int) {
        var currentValue = colorBrightness.progress / 100f
        var hsv = colorToHSV(rgb)
        hsv[VALUE_INDEX] = currentValue
        currentTile.setTextColor(Color.HSVToColor(hsv))
    }

    fun onChangeTileClick(_view: View) {
        var newTileId = colorGroup.checkedRadioButtonId
        if (currentTile.id != newTileId) {
            currentTile = findViewById(newTileId)
            var hsv = colorToHSV(currentTile.currentTextColor)
            var progress = (hsv[VALUE_INDEX] * 100).toInt()
            colorBrightness.progress = progress
            colorWheel.setHsv(hsv)
        }
    }

    private fun colorToHSV(color: Int): FloatArray{
        var hsv = floatArrayOf(0f, 0f, 0f)
        Color.colorToHSV(color, hsv)
        return hsv
    }
}
