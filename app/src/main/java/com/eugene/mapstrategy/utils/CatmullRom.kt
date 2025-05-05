package com.eugene.mapstrategy.utils

import com.amap.api.maps.model.LatLng

object CatmullRom {
  /**
   * 使用Catmull-Rom样条插值生成平滑曲线点集合
   * @param originalPoints 原始点集合
   * @param pointsPerSegment 每两个原始点之间生成的插值点数
   * @return 平滑后的点集合
   */
  @JvmStatic
  fun createSmoothCurve(originalPoints: List<LatLng>, pointsPerSegment: Int): List<LatLng> {
    val smoothedPoints: MutableList<LatLng> = ArrayList()
    if (originalPoints.size < 2) {
      return originalPoints
    }

    // 扩展点列表，首尾添加虚拟点以保证曲线经过所有原始点
    val extendedPoints: MutableList<LatLng> = ArrayList()
    extendedPoints.add(originalPoints[0])
    extendedPoints.addAll(originalPoints)
    extendedPoints.add(originalPoints[originalPoints.size - 1])

    // 生成插值点
    for (i in 1 until extendedPoints.size - 2) {
      val p0 = extendedPoints[i - 1]
      val p1 = extendedPoints[i]
      val p2 = extendedPoints[i + 1]
      val p3 = extendedPoints[i + 2]

      for (j in 0..pointsPerSegment) {
        val t = j.toFloat() / pointsPerSegment
        val point = catmullRom(p0, p1, p2, p3, t)
        smoothedPoints.add(point)
      }
    }
    return smoothedPoints
  }


  // Catmull-Rom插值算法
  @JvmStatic
  private fun catmullRom(p0: LatLng, p1: LatLng, p2: LatLng, p3: LatLng, t: Float): LatLng {
    val t2 = (t * t).toDouble()
    val t3 = t2 * t

    val lng = 0.5 * ((2 * p1.longitude) + (
        (-p0.longitude + p2.longitude) * t) + (
        (2 * p0.longitude - 5 * p1.longitude + 4 * p2.longitude - p3.longitude) * t2) + (
        (-p0.longitude + 3 * p1.longitude - 3 * p2.longitude + p3.longitude) * t3))

    val lat = 0.5 * ((2 * p1.latitude) + (
        (-p0.latitude + p2.latitude) * t) + (
        (2 * p0.latitude - 5 * p1.latitude + 4 * p2.latitude - p3.latitude) * t2) + (
        (-p0.latitude + 3 * p1.latitude - 3 * p2.latitude + p3.latitude) * t3))

    return LatLng(lat, lng)
  }
}