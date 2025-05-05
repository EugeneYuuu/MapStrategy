package com.eugene.mapstrategy.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.Drawable

/**
 * @author EugeneYu
 * @date 2025/3/7
 * @desc 自定义文本样式Drawable
 */
class TextShapeDrawable(context: Context?): Drawable() {
  private var mContext = context
  private var mPaint: Paint
  private var mText: String? = null
  private var mCirclePaint: Paint

  init {
    mPaint = Paint()
    mCirclePaint = Paint().apply {
      color = "#CFDAE2".toColor()
      isAntiAlias = true
    }
  }

  fun setMarkText(text: String, typeFace: Typeface?) {
    mText = text
    mPaint.apply {
      color = "#2E448D".toColor()
      textSize = TUtil.dp2px(12f).toFloat()
      typeface = typeFace
    }
  }

  override fun draw(canvas: Canvas) {
    if (mText == null) {
      return
    }

    val radius = 30f
    val centerX = bounds.centerX().toFloat()
    val centerY = bounds.centerY().toFloat()
    canvas.drawCircle(centerX, centerY, radius, mCirclePaint)

    val textWidth = mPaint.measureText(mText)
    val textHeight = mPaint.descent() - mPaint.ascent()
    val textOffsetX = textWidth / 2
    val textOffsetY = textHeight / 2 - mPaint.descent()
    canvas.drawText(mText ?: "", centerX - textOffsetX, centerY + textOffsetY, mPaint)
  }

  override fun setAlpha(p0: Int) {
    return
  }

  override fun setColorFilter(p0: ColorFilter?) {
    return
  }

  override fun getOpacity(): Int {
    return PixelFormat.UNKNOWN
  }
}