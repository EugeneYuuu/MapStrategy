package com.eugene.mapstrategy.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.children
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.utils.gone
import com.eugene.mapstrategy.utils.toColor
import com.eugene.mapstrategy.utils.visible

class ColorView (
  context: Context,
  private val isFirst: Boolean,
  private val drawView: DrawView?
) : FrameLayout(context) {

  private val ibColor: ImageButton
  private val ivCircle: ImageView

  init {
    // 使用 LayoutInflater 将布局文件膨胀并添加到当前视图中
    LayoutInflater.from(context).inflate(R.layout.view_color, this, true)
    ibColor = findViewById(R.id.ib_color)
    ivCircle = findViewById(R.id.iv_circle)
  }

  fun bindData(color: String, spColor: String?, block: (color: String) -> Unit) {
    // 创建圆形背景
    val shape = GradientDrawable()
    shape.shape = GradientDrawable.OVAL
    ibColor.background = shape
    // 设置圆形背景的颜色
    val drawable = ibColor.background as? GradientDrawable
    drawable?.setColor(color.toColor())

    if (isFirst && spColor == null) {
      ivCircle.visible()
    } else {
      ivCircle.gone()
    }

    if (spColor != null && color == spColor) {
      handleColorButtonClick()
      drawView?.setPaintColor(spColor.toColor())
    }

    ibColor.setOnClickListener {
      handleColorButtonClick()
      drawView?.setPaintColor(color.toColor())
      block.invoke(color)
    }
  }

  // 设置列表里的按钮的互斥
  private fun handleColorButtonClick() {
    if (ivCircle.visibility == View.GONE) {
      ivCircle.visible()

      // 隐藏列表中的其他ivCircle
      (parent as? ViewGroup)?.children?.forEach { child ->
        if (child is ColorView && child != this) {
          child.ivCircle.gone()
        }
      }
    }
  }
}