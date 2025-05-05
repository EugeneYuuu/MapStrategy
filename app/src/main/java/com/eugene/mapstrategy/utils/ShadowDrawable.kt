package com.eugene.mapstrategy.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.graphics.Path.Direction.CW
import android.graphics.PixelFormat
import android.graphics.PorterDuff.Mode.DST_ATOP
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.view.View
import androidx.core.view.ViewCompat
import kotlin.math.min

class ShadowDrawable private constructor(
  private val mShape: Int,
  bgColor: IntArray,
  bgFrameColor: Int,
  bgFrameRadius: Int,
  shapeRadius: IntArray,
  shadowColor: Int,
  shadowRadius: Int,
  offsetX: Int,
  offsetY: Int,
  orientation: Int
) : Drawable() {

  private val mShadowPaint: Paint
  private val mBgPaint: Paint
  private val mBgFramePaint: Paint
  private val mShadowRadius: Int
  private var mShapeRadius: IntArray
  private val mOffsetX: Int
  private val mOffsetY: Int
  private val mBgColor: IntArray?
  private val mBgFrameRadius: Int
  private var mRect: RectF? = null
  private val orientation: Int

  init {
    this.mShapeRadius = intArrayOf(0)
    this.mBgColor = bgColor
    this.mShapeRadius = shapeRadius
    this.mShadowRadius = shadowRadius
    this.mOffsetX = offsetX
    this.mOffsetY = offsetY
    this.mBgFrameRadius = bgFrameRadius
    this.orientation = orientation
    this.mShadowPaint = Paint()
    mShadowPaint.color = 0
    mShadowPaint.isAntiAlias = true
    mShadowPaint.setShadowLayer(
      shadowRadius.toFloat(),
      offsetX.toFloat(),
      offsetY.toFloat(),
      shadowColor
    )
    mShadowPaint.setXfermode(PorterDuffXfermode(DST_ATOP))
    this.mBgPaint = Paint()
    mBgPaint.isAntiAlias = true
    this.mBgFramePaint = Paint()
    mBgFramePaint.color = bgFrameColor
    mBgFramePaint.style = STROKE
    mBgFramePaint.strokeWidth = bgFrameRadius.toFloat()
    mBgFramePaint.isAntiAlias = true
  }

  override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
    super.setBounds(left, top, right, bottom)
    this.mRect = RectF(
      (left + this.mShadowRadius - this.mOffsetX).toFloat(),
      (top + this.mShadowRadius - this.mOffsetY).toFloat(),
      (right - this.mShadowRadius - this.mOffsetX).toFloat(),
      (bottom - this.mShadowRadius - this.mOffsetY).toFloat()
    )
  }

  override fun draw(canvas: Canvas) {
    if (this.mBgColor != null) {
      if (mBgColor.size == 1) {
        mBgPaint.color = mBgColor[0]
      } else if (this.orientation == 1) {
        mBgPaint.setShader(
          LinearGradient(
            mRect!!.left,
            mRect!!.top,
            mRect!!.left,
            mRect!!.bottom, this.mBgColor, null as FloatArray?, CLAMP
          )
        )
      } else if (this.orientation == 2) {
        mBgPaint.setShader(
          LinearGradient(
            mRect!!.left,
            mRect!!.height() / 2.0f,
            mRect!!.right,
            mRect!!.height() / 2.0f, this.mBgColor, null as FloatArray?, CLAMP
          )
        )
      } else if (this.orientation == 3) {
        mBgPaint.setShader(
          LinearGradient(
            mRect!!.left,
            mRect!!.top,
            mRect!!.right,
            mRect!!.bottom, this.mBgColor, null as FloatArray?, CLAMP
          )
        )
      } else if (this.orientation == 4) {
        mBgPaint.setShader(
          LinearGradient(
            mRect!!.right,
            mRect!!.top,
            mRect!!.left,
            mRect!!.bottom, this.mBgColor, null as FloatArray?, CLAMP
          )
        )
      }
    }

    if (this.mShape == 1) {
      if (this.mShapeRadius != null && mShapeRadius.size == 1) {
        canvas.drawRoundRect(
          mRect!!,
          mShapeRadius[0].toFloat(),
          mShapeRadius[0].toFloat(), this.mShadowPaint
        )
        canvas.drawRoundRect(
          mRect!!.left + (this.mBgFrameRadius / 2).toFloat(),
          mRect!!.top + (this.mBgFrameRadius / 2).toFloat(),
          mRect!!.right - (this.mBgFrameRadius / 2).toFloat(),
          mRect!!.bottom - (this.mBgFrameRadius / 2).toFloat(),
          mShapeRadius[0].toFloat(),
          mShapeRadius[0].toFloat(), this.mBgFramePaint
        )
        canvas.drawRoundRect(
          mRect!!.left + (this.mBgFrameRadius / 2).toFloat(),
          mRect!!.top + (this.mBgFrameRadius / 2).toFloat(),
          mRect!!.right - (this.mBgFrameRadius / 2).toFloat(),
          mRect!!.bottom - (this.mBgFrameRadius / 2).toFloat(),
          mShapeRadius[0].toFloat(),
          mShapeRadius[0].toFloat(), this.mBgPaint
        )
      } else if (mShapeRadius.size == 4) {
        val path = Path()
        val radiusArray = floatArrayOf(
          mShapeRadius[0].toFloat(),
          mShapeRadius[0].toFloat(),
          mShapeRadius[1].toFloat(),
          mShapeRadius[1].toFloat(),
          mShapeRadius[2].toFloat(),
          mShapeRadius[2].toFloat(),
          mShapeRadius[3].toFloat(), mShapeRadius[3].toFloat()
        )
        path.addRoundRect(mRect!!, radiusArray, CW)
        canvas.drawPath(path, this.mShadowPaint)
        path.addRoundRect(
          RectF(
            mRect!!.left + (this.mBgFrameRadius / 2).toFloat(),
            mRect!!.top + (this.mBgFrameRadius / 2).toFloat(),
            mRect!!.right - (this.mBgFrameRadius / 2).toFloat(),
            mRect!!.bottom - (this.mBgFrameRadius / 2).toFloat()
          ), radiusArray, CW
        )
        canvas.drawPath(path, this.mBgFramePaint)
        path.addRoundRect(
          RectF(
            mRect!!.left + (this.mBgFrameRadius / 2).toFloat(),
            mRect!!.top + (this.mBgFrameRadius / 2).toFloat(),
            mRect!!.right - (this.mBgFrameRadius / 2).toFloat(),
            mRect!!.bottom - (this.mBgFrameRadius / 2).toFloat()
          ), radiusArray, CW
        )
        canvas.drawPath(path, this.mBgPaint)
      }
    } else {
      canvas.drawCircle(
        mRect!!.centerX(),
        mRect!!.centerY(), (min(
          mRect!!.width().toDouble(),
          mRect!!.height().toDouble()
        ) / 2.0f).toFloat(), this.mShadowPaint
      )
      canvas.drawCircle(
        mRect!!.centerX(),
        mRect!!.centerY(),
        (min(
          (mRect!!.width() - mBgFrameRadius.toFloat()).toDouble(),
          (mRect!!.height() - mBgFrameRadius.toFloat()).toDouble()
        ) / 2.0f).toFloat(),
        this.mBgFramePaint
      )
      canvas.drawCircle(
        mRect!!.centerX(),
        mRect!!.centerY(),
        (min(
          (mRect!!.width() - mBgFrameRadius.toFloat()).toDouble(),
          (mRect!!.height() - mBgFrameRadius.toFloat()).toDouble()
        ) / 2.0f).toFloat(),
        this.mBgPaint
      )
    }
  }

  override fun setAlpha(alpha: Int) {
    mShadowPaint.alpha = alpha
  }

  override fun setColorFilter(colorFilter: ColorFilter?) {
    mShadowPaint.setColorFilter(colorFilter)
  }

  override fun getOpacity(): Int {
    return PixelFormat.TRANSLUCENT
  }

  class DrawableBuilder {
    private var mShape = 1
    private var mShapeRadius = intArrayOf(0)
    private var mFrameColor: Int
    private var mFrameRadius: Int
    private var mShadowColor = Color.parseColor("#00000000")
    private var mShadowRadius = 0
    private var mOffsetX = 0
    private var mOffsetY = 0
    private var mBgColor = IntArray(1)
    private var orientation: Int

    init {
      mBgColor[0] = 0
      this.mFrameColor = 0
      this.mFrameRadius = 0
      this.orientation = 2
    }

    fun setOrientation(orientation: Int): DrawableBuilder {
      this.orientation = orientation
      return this
    }

    fun setShape(mShape: Int): DrawableBuilder {
      this.mShape = mShape
      return this
    }

    fun setShapeRadius(ShapeRadius: Int): DrawableBuilder {
      this.mShapeRadius = intArrayOf(ShapeRadius)
      return this
    }

    fun setShapeRadius(
      topLeft: Int,
      topRight: Int,
      bottomRight: Int,
      bottomLeft: Int
    ): DrawableBuilder {
      this.mShapeRadius = intArrayOf(topLeft, topRight, bottomRight, bottomLeft)
      return this
    }

    fun setShadowColor(shadowColor: Int): DrawableBuilder {
      this.mShadowColor = shadowColor
      return this
    }

    fun setShadowRadius(shadowRadius: Int): DrawableBuilder {
      this.mShadowRadius = shadowRadius
      return this
    }

    fun setOffsetX(OffsetX: Int): DrawableBuilder {
      this.mOffsetX = OffsetX
      return this
    }

    fun setOffsetY(OffsetY: Int): DrawableBuilder {
      this.mOffsetY = OffsetY
      return this
    }

    fun setBgColor(BgColor: Int): DrawableBuilder {
      mBgColor[0] = BgColor
      return this
    }

    fun setBgColor(BgColor: IntArray): DrawableBuilder {
      this.mBgColor = BgColor
      return this
    }

    fun setBgFrameColor(frameColor: Int): DrawableBuilder {
      this.mFrameColor = frameColor
      return this
    }

    fun setBgFrameRadius(frameRadius: Int): DrawableBuilder {
      this.mFrameRadius = frameRadius
      return this
    }

    fun into(view: View) {
      val drawable = ShadowDrawable(
        this.mShape,
        this.mBgColor,
        this.mFrameColor,
        this.mFrameRadius,
        this.mShapeRadius,
        this.mShadowColor,
        this.mShadowRadius,
        this.mOffsetX,
        this.mOffsetY,
        this.orientation
      )
      if (VERSION.SDK_INT < 28 && this.mShadowRadius != 0) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null as Paint?)
      } else {
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null as Paint?)
      }

      ViewCompat.setBackground(view, drawable)
    }
  }

  companion object {
    const val SHAPE_ROUND: Int = 1
    const val SHAPE_CIRCLE: Int = 2
    const val TOP_TO_BOTTOM: Int = 1
    const val LEFT_TO_RIGHT: Int = 2
    const val LEFTTOP_TO_RIGHTDOWN: Int = 3
    const val RIGHTTOP_TO_LEFTTDOWN: Int = 4

    fun Builder(): DrawableBuilder {
      return DrawableBuilder()
    }

    fun setShadowDrawable(view: View, drawable: Drawable?) {
      view.setLayerType(View.LAYER_TYPE_SOFTWARE, null as Paint?)
      ViewCompat.setBackground(view, drawable)
    }
  }
}