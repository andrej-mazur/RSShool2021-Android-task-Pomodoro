package com.example.pomodoro

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes

class CountdownTimerCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var initTime = 0L

    var currentTime = 0L
        set(value) {
            field = value
            invalidate()
        }

    private var color = DEFAULT_COLOR

    private var style = DEFAULT_STYLE

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        if (attrs != null) {
            val styledAttrs = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CircleTimerView,
                defStyleAttr,
                0
            )
            color = styledAttrs.getColor(R.styleable.CircleTimerView_custom_color, DEFAULT_COLOR)
            style = styledAttrs.getInt(R.styleable.CircleTimerView_custom_style, DEFAULT_STYLE)
            styledAttrs.recycle()
        }

        paint.color = color
        paint.style = if (style == DEFAULT_STYLE) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = 5F
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (initTime == 0L || currentTime == 0L) return
        val startAngle = -90f
        val sweepAngle = -if (initTime == currentTime) {
            360F
        } else {
            ((currentTime % initTime).toFloat() / initTime) * 360
        }

        canvas.drawArc(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            startAngle,
            sweepAngle,
            true,
            paint
        )
    }

    private companion object {
        private const val DEFAULT_COLOR = Color.RED
        private const val DEFAULT_STYLE = 0
    }
}
