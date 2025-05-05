package com.eugene.mapstrategy.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.amap.api.maps.MapView
import com.amap.api.maps.model.Marker
import com.eugene.mapstrategy.MyApplication.Companion.getApplication
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.utils.ShadowDrawable
import com.eugene.mapstrategy.utils.TUtil
import com.eugene.mapstrategy.utils.TextShapeDrawable
import com.eugene.mapstrategy.utils.gone
import com.eugene.mapstrategy.utils.visible

/**
 * @author EugeneYu
 * @date 2025/1/8
 * @desc 绘制工具类
 */
class ToolsView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

  private var drawView: DrawView? = null
  private val includePaintControls: View
  private val paintBtn: ImageButton
  private val eraseBtn: ImageButton
  private val fingerBtn: ImageButton
  private val stickersBtn: ImageButton
  private val llPaintContainer: LinearLayout
  private val llPaintColor: LinearLayout
  private val paintShadow: View
  private val paintColorShadow: View
  private var circleView: CircleView? = null
  private var lineSeekBar: SeekBar? = null

  private var paintClickCount: Int = 0
  private var lineSliderClickCount: Int = 0
  private var stickersClickCount: Int = 0
  private var innerRadius = 7f
  private var marker: Marker? = null

  // 画笔宽度颜色保存本地历史
  private val sp = context.getSharedPreferences("color&stroke", Context.MODE_PRIVATE)

  private val colors = arrayOf(
    "#FF000000",
    "#FF0000",
    "#0073FF",
    "#03DAC5",
  )

  init {
    LayoutInflater.from(context).inflate(R.layout.view_tools, this, true)

    includePaintControls = findViewById(R.id.include_paint_controls)
    llPaintColor = includePaintControls.findViewById(R.id.ll_paint_color)
    paintColorShadow = includePaintControls.findViewById(R.id.paint_color_shadow)
    lineSeekBar = includePaintControls.findViewById(R.id.line_slider_bar)

    paintBtn = findViewById(R.id.ib_paint)
    eraseBtn = findViewById(R.id.ib_erase)
    fingerBtn = findViewById(R.id.ib_finger)
    stickersBtn = findViewById(R.id.ib_stickers)
    llPaintContainer = findViewById(R.id.ll_paint_container)
    paintShadow = findViewById(R.id.paint_shadow)

    paintBtn.isSelected = false
    eraseBtn.isSelected = false
    fingerBtn.isSelected = true
    stickersBtn.isSelected = false

    paintBtn.setOnClickListener(this)
    eraseBtn.setOnClickListener(this)
    fingerBtn.setOnClickListener(this)
    stickersBtn.setOnClickListener(this)
  }

  fun bindData() {
    val spColor = sp?.getString("paintColor", null)
    innerRadius = sp?.getString("paintStroke", null)?.toFloat() ?: innerRadius

    llPaintColor.apply {
      removeAllViews()
      // 动态添加画笔颜色组件
      for (i in colors.indices) {
        val colorView = ColorView(context, i == 0, drawView)
        colorView.bindData(colors[i], spColor) { color ->
          lineSeekBar.gone()
          sp?.edit()?.putString("paintColor", color)?.apply()
        }
        addView(colorView)
      }
      // 添加分割线
      val divider = View(context).apply {
        layoutParams = LayoutParams(
          TUtil.dp2px(1f),
          LayoutParams.MATCH_PARENT
        ).apply {
          setMargins(6, 10, 6, 10)
        }
        setBackgroundColor(ContextCompat.getColor(context, R.color.divider_color))
      }
      addView(divider)

      circleView = CircleView(context).apply {
        layoutParams = LayoutParams(TUtil.dp2px(24f), TUtil.dp2px(24f))
        outerColor = ContextCompat.getColor(context, R.color.gray)
        innerColor = ContextCompat.getColor(context, R.color.white)
        setOuterRadius(TUtil.dp2px(12f).toFloat())
        setOnClickListener {
          lineSliderClickCount++
          if (lineSliderClickCount % 2 == 0) {
            lineSeekBar.gone()
          } else {
            lineSeekBar.visible()
          }
        }
      }
      addView(circleView)
    }

    lineSeekBar?.apply {
      max = 10 // progress max
      min = 1 // progress min

      // 设置滑块的初始样式
      progress = sp?.getString("progress", null)?.toInt() ?: 2
      val initialTextDrawable = TextShapeDrawable(context)
      initialTextDrawable.setMarkText(progress.toString(), Typeface.DEFAULT_BOLD)
      thumb = initialTextDrawable
      circleView?.setInnerRadius(TUtil.dp2px(innerRadius).toFloat())
      drawView?.setPaintStrokeWidth(innerRadius)

      setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(
          seekBar: SeekBar?,
          progress: Int,
          fromUser: Boolean
        ) {
          // 设置滑动过程中的滑块样式
          val textDrawable = TextShapeDrawable(context)
          textDrawable.setMarkText(progress.toString(), Typeface.DEFAULT_BOLD)
          thumb = textDrawable

          // Set the inner radius of the circle view
          innerRadius = when (progress) {
            1 -> 5f
            2 -> 7f
            3 -> 8f
            4 -> 9f
            5 -> 10f
            6 -> 12f
            7 -> 14f
            8 -> 16f
            9 -> 18f
            10 -> 20f
            else -> 7f
          }
          circleView?.setInnerRadius(TUtil.dp2px(innerRadius).toFloat())
          drawView?.setPaintStrokeWidth(innerRadius)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
          // 停止滑动，将画笔颜色和宽度保存在本地
          sp?.edit()?.putString("paintStroke", innerRadius.toString())?.apply()
          sp?.edit()?.putString("progress", progress.toString())?.apply()
        }
      })
    }

    ShadowDrawable.Builder()
      .setShadowColor(getApplication().resources.getColor(R.color.color_24000000))
      .setShadowRadius(TUtil.dp2px(20f))
      .setOffsetY(TUtil.dp2px(2f))
      .into(paintShadow)
  }

  fun setDrawView(drawView: DrawView?) {
    this.drawView = drawView
  }

  fun controlStickers(zoom: Float?) {
    zoom?.let {
      if (zoom >= 18f) {
        stickersBtn.visible()
      } else {
        stickersBtn.gone()
      }
    }
  }

  fun setMarker(marker: Marker?) {
    this.marker = marker
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.ib_paint -> {
        paintClickCount++
        setSelectBtn(paintBtn)
        drawView?.setEraserMode(false)
      }
      R.id.ib_erase -> {
        setSelectBtn(eraseBtn)
        drawView?.clearBoard()
        drawView?.setEraserMode(true)
      }
      R.id.ib_finger -> {
        setSelectBtn(fingerBtn)
        drawView?.setEraserMode(false)
      }
      R.id.ib_stickers -> {
        stickersClickCount++
        setSelectBtn(stickersBtn)
        drawView?.setEraserMode(false)
      }
    }
  }

  private fun setSelectBtn(selectBtn: ImageButton) {
    selectBtn.isSelected = true

    when (selectBtn) {
      paintBtn -> {
        lineSliderClickCount = 0
        stickersClickCount = 0
        eraseBtn.isSelected = false
        fingerBtn.isSelected = false
        stickersBtn.isSelected = false
      }
      eraseBtn -> {
        paintClickCount = 0
        lineSliderClickCount = 0
        stickersClickCount = 0
        paintBtn.isSelected = false
        fingerBtn.isSelected = false
        stickersBtn.isSelected = false
      }
      fingerBtn -> {
        paintClickCount = 0
        lineSliderClickCount = 0
        stickersClickCount = 0
        paintBtn.isSelected = false
        eraseBtn.isSelected = false
        stickersBtn.isSelected = false
      }
      stickersBtn -> {
        paintClickCount = 0
        lineSliderClickCount = 0
        paintBtn.isSelected = false
        eraseBtn.isSelected = false
        fingerBtn.isSelected = false
      }
    }

    if (paintBtn.isSelected || eraseBtn.isSelected || fingerBtn.isSelected) {
      marker?.remove()
    }

    if (!paintBtn.isSelected || paintClickCount % 2 == 1) {
      llPaintColor.gone()
      lineSeekBar.gone()
    } else if (paintClickCount % 2 == 0) {
      llPaintColor.visible()
    }

    if (fingerBtn.isSelected || stickersBtn.isSelected) {
      drawView?.gone()
      drawView?.clearBoard()
    } else {
      drawView?.visible()
    }
  }
}