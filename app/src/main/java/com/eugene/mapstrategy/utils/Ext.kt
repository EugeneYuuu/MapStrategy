package com.eugene.mapstrategy.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Polyline

fun <T:Polyline> List<T>.visibles(){
  this.all {
    it.isVisible = true
    true
  }
}

fun <T:Polyline> List<T>.gones(){
  this.all {
    it.isVisible = false
    true
  }
}

fun <T: Polyline> List<T>.isVisible(): Boolean {
  return this.all { it.isVisible }
}

fun <T:View> T?.visible(){
  this?.visibility = View.VISIBLE
}

fun <T:View> T?.gone(){
  this?.visibility = View.GONE
}

/**
 * 设置View的margin
 */
fun <T: View> T?.setMargins(marginStart: Int = 0, marginTop: Int = 0, marginEnd: Int = 0, marginBottom: Int = 0) {
  val layoutParams = this?.layoutParams as ViewGroup.MarginLayoutParams
  layoutParams.setMargins(marginStart, marginTop, marginEnd, marginBottom)
  this.layoutParams = layoutParams
}

/**
 * 生成地理坐标镜像点
 */
fun LatLng.mirror(reference: LatLng) = LatLng(
  2 * latitude - reference.latitude,
  2 * longitude - reference.longitude
)

fun Double.roundTo(decimal: Int) =
  "%.${decimal}f".format(this).toDouble()

fun String.toColor() =
  Color.parseColor(this)

// 定义一个扩展函数，用于将 Drawable 转换为 Bitmap
fun Drawable.toBitmap(): Bitmap {
  if (this is BitmapDrawable) {
    return this.bitmap
  }
  val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  setBounds(0, 0, canvas.width, canvas.height)
  draw(canvas)
  return bitmap
}

// 定义一个扩展函数，用于缩放 Bitmap
fun Bitmap.scaleBitmap(newWidth: Int, newHeight: Int): Bitmap {
  return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
}