package com.eugene.mapstrategy.utils

import android.content.Context
import android.content.Intent
import android.graphics.Point
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.Projection
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.eugene.mapstrategy.MyApplication.Companion.getApplication
import java.io.IOException

object TUtil {
  /**
   * 获取状态栏高度
   */
  fun getStatusBarHeight(context: Context): Int {
    val resourceId = context.resources.getIdentifier(
      "status_bar_height", "dimen", "android"
    )
    return if (resourceId > 0) {
      context.resources.getDimensionPixelSize(resourceId)
    } else 0
  }

  fun dp2px(dpVal: Float): Int {
    val scale = getApplication().resources.displayMetrics.density
    return (0.5f + dpVal * scale).toInt()
  }

  fun jumpToActivity(context: Context?, cls: Class<*>?) {
    val intent = Intent(context, cls)
    context?.startActivity(intent)
  }

  fun convertToLatLonPoint(latLng: LatLng?): LatLonPoint  {
    if (latLng == null) return LatLonPoint(0.0, 0.0)
    return LatLonPoint(latLng.latitude, latLng.longitude)
  }


  /**
   * 将屏幕坐标转换为地理坐标
   */
  fun convertPointToLatLng(projection: Projection?, points: MutableList<Point?>): List<LatLng> {
    val geoPoints = mutableListOf<LatLng>()
    projection?.let {
      points.forEach { point ->
        val latLng = it.fromScreenLocation(point)
        geoPoints.add(latLng)
      }
    }
    points.clear()
    return geoPoints
  }

  /**
   * 计算线段与圆是否相交
   */
  fun isLineIntersectingCircle(
    pointA: LatLng,
    pointB: LatLng,
    center: LatLng,
    eraserRadiusMeters: Float
  ): Boolean {
    // 检查端点是否在圆内
    if (AMapUtils.calculateLineDistance(pointA, center) <= eraserRadiusMeters ||
      AMapUtils.calculateLineDistance(pointB, center) <= eraserRadiusMeters
    ) {
      return true
    }

    // 计算线段到圆心的最近距离
    val closestPoint = getClosestPointOnSegment(pointA, pointB, center)
    return AMapUtils.calculateLineDistance(closestPoint, center) <= eraserRadiusMeters
  }

  /**
   * 计算线段上距离某点最近的点
   */
  fun getClosestPointOnSegment(
    A: LatLng, B: LatLng, P: LatLng
  ): LatLng {
    val aToB = LatLng(B.latitude - A.latitude, B.longitude - A.longitude)
    val aToP = LatLng(P.latitude - A.latitude, P.longitude - A.longitude)

    val atb2 = aToB.latitude * aToB.latitude + aToB.longitude * aToB.longitude
    val atpDotAtb = aToP.latitude * aToB.latitude + aToP.longitude * aToB.longitude

    var t = atpDotAtb / atb2
    t = t.coerceIn(0.0, 1.0)

    return LatLng(
      A.latitude + t * aToB.latitude,
      A.longitude + t * aToB.longitude
    )
  }

  /**
   * 计算每像素对应的米数
   */
  fun getMetersPerPixel(projection: Projection, width: Int, height: Int): Float {
    // 获取屏幕中心点坐标
    val screenCenter = Point(width / 2, height / 2)
    val centerLatLng = projection.fromScreenLocation(screenCenter)

    // 取一个水平方向偏移100像素的点
    val offsetPoint = Point(screenCenter.x + 100, screenCenter.y)
    val offsetLatLng = projection.fromScreenLocation(offsetPoint)

    // 计算两个经纬度点之间的实际距离（米）
    val distanceMeters = AMapUtils.calculateLineDistance(centerLatLng, offsetLatLng)

    // 计算每像素对应的米数
    return distanceMeters / 100f
  }

  fun jsonFileOperate(context: Context?, fileName: String): String {
    val jsonData = StringBuilder()
    try {
      context?.assets?.open(fileName)?.bufferedReader().use { reader ->
        var line: String?
        while ((reader?.readLine().also { line = it }) != null) {
          jsonData.append(line)
        }
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return jsonData.toString()
  }
}