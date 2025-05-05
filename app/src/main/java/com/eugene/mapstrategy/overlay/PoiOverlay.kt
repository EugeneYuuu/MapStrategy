package com.eugene.mapstrategy.overlay

import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.PoiItem


/**
 * @author EugeneYu
 * @date 2025/3/19
 * @desc Poi图层类。在高德地图API里，如果要显示Poi，可以用此类来创建Poi图层。如不满足需求，也可以自己创建自定义的Poi图层
 */
class PoiOverlay(private val mAMap: AMap?, pois: List<PoiItem>?) {
  private val mPois: List<PoiItem>? = pois
  private val mPoiMarks = ArrayList<Marker>()

  /**
   * 添加Marker到地图中。
   */
  fun addToMap() {
    try {
      for (i in mPois!!.indices) {
        val marker = mAMap?.addMarker(getMarkerOptions(i))
        marker?.setObject(i)
        mPoiMarks.add(marker!!)
      }
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }

  /**
   * 去掉PoiOverlay上所有的Marker。
   */
  fun removeFromMap() {
    for (mark in mPoiMarks) {
      mark.remove()
    }
    mPoiMarks.clear()
  }

  /**
   * 移动镜头到当前的视角。
   */
  fun zoomToSpan() {
    try {
      if (!mPois.isNullOrEmpty()) {
        if (mAMap == null) return
        if (mPois.size == 1) {
          mAMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
              LatLng(
                mPois[0].latLonPoint.latitude,
                mPois[0].latLonPoint.longitude
              ), 17f
            )
          )
        } else {
          val bounds = latLngBounds
          mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30))
        }
      }
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }

  private val latLngBounds: LatLngBounds
    get() {
      val b = LatLngBounds.builder()
      for (i in mPois!!.indices) {
        b.include(
          LatLng(
            mPois[i].latLonPoint.latitude,
            mPois[i].latLonPoint.longitude
          )
        )
      }
      return b.build()
    }

  private fun getMarkerOptions(index: Int): MarkerOptions {
    return MarkerOptions()
      .position(
        LatLng(
          mPois!![index].latLonPoint
            .latitude, mPois[index]
            .latLonPoint.longitude
        )
      )
      .title(getTitle(index)).snippet(getSnippet(index))
      .icon(getBitmapDescriptor(index))
  }

  /**
   * 给第几个Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
   * @param index 第几个Marker。
   * @return 更换的Marker图片。
   */
  protected fun getBitmapDescriptor(index: Int): BitmapDescriptor? {
    return null
  }

  /**
   * 返回第index的Marker的标题。
   * @param index 第几个Marker。
   * @return marker的标题。
   */
  protected fun getTitle(index: Int): String {
    return mPois!![index].title
  }

  /**
   * 返回第index的Marker的详情。
   * @param index 第几个Marker。
   * @return marker的详情。
   */
  protected fun getSnippet(index: Int): String {
    return mPois!![index].snippet
  }

  /**
   * 从marker中得到poi在list的位置。
   * @param marker 一个标记的对象。
   * @return 返回该marker对应的poi在list的位置。
   * @since V2.1.0
   */
  fun getPoiIndex(marker: Marker): Int {
    for (i in mPoiMarks.indices) {
      if (mPoiMarks[i] == marker) {
        return i
      }
    }
    return -1
  }

  /**
   * 返回第index的poi的信息。
   * @param index 第几个poi。
   * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 **[PoiItem](../../../../../../Search/com/amap/api/services/core/PoiItem.html)**。
   */
  fun getPoiItem(index: Int): PoiItem? {
    if (index < 0 || index >= mPois!!.size) {
      return null
    }
    return mPois[index]
  }
}
