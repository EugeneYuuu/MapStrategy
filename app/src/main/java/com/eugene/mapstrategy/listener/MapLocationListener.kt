package com.eugene.mapstrategy.listener

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.LocationSource.OnLocationChangedListener
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.MyLocationStyle.LOCATION_TYPE_LOCATE
import com.google.android.gms.location.LocationServices

/**
 * @author EugeneYu
 * @date 2024/12/31
 * @desc 地图定位类
 */
class MapLocationListener(context: Context?, private val aMap: AMap?): LocationSource, AMapLocationListener {
  private val mContext = context
  private var mLocationClient: AMapLocationClient? = null
  private var mLocationOption: AMapLocationClientOption? = null
  private val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext!!)
  private var mListener: OnLocationChangedListener?= null
  private var currentLocation: LatLng? = null

  private val STROKE_COLOR = Color.argb(180, 3, 145, 255)
  private val FILL_COLOR = Color.argb(10, 0, 0, 180)

  private val sharedPreferences: SharedPreferences = mContext?.getSharedPreferences("location", Context.MODE_PRIVATE)!!

  fun getLocationClient(): AMapLocationClient? {
    return mLocationClient
  }

  fun getCurrentLocation(): LatLng? {
    return currentLocation
  }

  init {
    aMap?.uiSettings?.isMyLocationButtonEnabled = false // 设置默认定位按钮是否显示
    aMap?.uiSettings?.isZoomControlsEnabled = false
  }

  /**
   * 设置aMap的定位属性
   */
  fun initLocation() {
    aMap?.setLocationSource(this) // 设置定位监听
    aMap?.isMyLocationEnabled = true // 设置为true表示显示定位层并可触发定位
    setLocationStyle()

    // 检查是否有缓存的定位信息
    val lastLocation = getLastKnownLocation()
    if (lastLocation != null) {
      aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 18f))
    }
  }

  private fun setLocationStyle() {
    // 自定义系统定位蓝点
    val myLocationStyle = MyLocationStyle().apply {
      // 自定义定位蓝点图标
      showMyLocation(true)
      // 自定义精度范围的圆形边框颜色
      strokeColor(STROKE_COLOR)
      //自定义精度范围的圆形边框宽度
      strokeWidth(5f)
      // 设置圆形的填充颜色
      radiusFillColor(FILL_COLOR)
      LOCATION_TYPE_LOCATE
    }
    // 将自定义的 myLocationStyle 对象添加到地图上
    aMap?.myLocationStyle = myLocationStyle
  }

  fun setCustomLocation(location: ImageView?) {
    location?.setOnClickListener {
      // 触发定位
      aMap?.isMyLocationEnabled = true
      aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
    }
  }

  /**
   * 定位成功后回调函数
   */
  override fun onLocationChanged(amapLocation: AMapLocation?) {
    if (mListener != null && amapLocation != null) {
      if (amapLocation.errorCode == 0) {
        mListener?.onLocationChanged(amapLocation) // 显示系统小蓝点
        currentLocation = LatLng(amapLocation.latitude, amapLocation.longitude)
        aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
        saveLocation(amapLocation.latitude, amapLocation.longitude)
      } else {
        val errText = "定位失败," + amapLocation.errorCode + ": " + amapLocation.errorInfo
        Log.e("AmapErr", errText)
      }
    }
  }

  /**
   * 存储定位信息
   */
  private fun saveLocation(latitude: Double, longitude: Double) {
    with(sharedPreferences.edit()) {
      putFloat("latitude", latitude.toFloat())
      putFloat("longitude", longitude.toFloat())
      apply()
    }
  }

  /**
   * 获取最后一次定位
   */
  private fun getLastKnownLocation(): LatLng? {
    val latitude = sharedPreferences.getFloat("latitude", 0f).toDouble()
    val longitude = sharedPreferences.getFloat("longitude", 0f).toDouble()
    if (!latitude.isNaN() && !longitude.isNaN()) {
      return LatLng(latitude, longitude)
    } else {
      return null
    }
  }

  /**
   * 激活定位
   */
  override fun activate(listener: OnLocationChangedListener?) {
    mListener = listener
    if (mLocationClient == null) {
      // 初始化locationClient
      mLocationClient = AMapLocationClient(mContext)
      mLocationOption = AMapLocationClientOption()

      //设置定位监听
      mLocationClient?.setLocationListener(this)
      //设置为高精度定位模式
      mLocationOption?.setLocationMode(Hight_Accuracy)
      // 只是为了获取当前位置，所以设置为单次定位
      mLocationOption?.isOnceLocation = true
      // 启动定位时SDK会返回最近3s内精度最高的一次定位结果
      mLocationOption?.setOnceLocationLatest(true)
      //设置定位参数
      mLocationClient?.setLocationOption(mLocationOption)
      mLocationClient?.startLocation()
      Toast.makeText(mContext, "正在定位中...", Toast.LENGTH_SHORT).show()
    }
  }

  /**
   * 停止定位
   */
  override fun deactivate() {
    mListener = null
    if (mLocationClient != null) {
      mLocationClient?.stopLocation();
      mLocationClient?.onDestroy();
    }
    mLocationClient = null;
  }

  fun triggerDestroy() {
    if (aMap != null) {
      aMap.isMyLocationEnabled = false;
    }
  }
}