package com.eugene.mapstrategy.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * @author EugeneYu
 * @date 2025/2/26
 * @desc 线条宽度view
 */
class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private var outerRadius: Float = 0f
    private var innerRadius: Float = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    var outerColor: Int = Color.LTGRAY
        set(value) {
            field = value
            invalidate()
        }

    var innerColor: Int = Color.DKGRAY
        set(value) {
            field = value
            invalidate()
        }

    fun setOuterRadius(radius: Float) {
        this.outerRadius = radius
        invalidate()
    }

    fun setInnerRadius(radius: Float) {
        this.innerRadius = radius
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f

        // Draw the circle
        paint.color = outerColor
        canvas.drawCircle(centerX, centerY, outerRadius, paint)

        // Draw the center
        paint.color = innerColor
        canvas.drawCircle(centerX, centerY, innerRadius / 2, paint)
    }
}