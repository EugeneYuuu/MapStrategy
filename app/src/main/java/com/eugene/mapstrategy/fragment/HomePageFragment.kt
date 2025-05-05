package com.eugene.mapstrategy.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.OnCameraChangeListener
import com.amap.api.maps.AMap.OnMapClickListener
import com.amap.api.maps.AMap.OnMarkerClickListener
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.help.Tip
import com.eugene.mapstrategy.MyApplication.Companion.getApplication
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.activity.BaseActivity
import com.eugene.mapstrategy.activity.SearchActivity
import com.eugene.mapstrategy.adapter.MarkerInfoWindowAdapter
import com.eugene.mapstrategy.adapter.StickersInfoWindowAdapter
import com.eugene.mapstrategy.listener.MapLocationListener
import com.eugene.mapstrategy.listener.PoiSearchListener
import com.eugene.mapstrategy.utils.Constants
import com.eugene.mapstrategy.utils.ShadowDrawable
import com.eugene.mapstrategy.utils.TUtil
import com.eugene.mapstrategy.utils.gone
import com.eugene.mapstrategy.utils.scaleBitmap
import com.eugene.mapstrategy.utils.setMargins
import com.eugene.mapstrategy.utils.visible
import com.eugene.mapstrategy.view.DrawView
import com.eugene.mapstrategy.view.ToolsView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomePageFragment: BaseFragment(),
  OnCameraChangeListener, OnMarkerClickListener,
  OnClickListener, OnMapClickListener {

  private var drawView: DrawView? = null
  private var mapView: MapView? = null
  private var aMap: AMap? = null
  private var toolsView: ToolsView? = null
  private var zoomLevel: TextView? = null
  private var locationShadow: View? = null
  private var controlShadow: View? = null
  private var ivLocation: ImageView? = null
  private var ibControl: ImageButton? = null
  private var rlSearch: RelativeLayout? = null
  private var keywordsSearch: TextView? = null
  private var cleanKeyWords: ImageView? = null
  private var mPoiMarker: Marker? = null
  private var mStickerMarker: Marker? = null

  private var mapLocationListener: MapLocationListener? = null
  private var poiSearchListener: PoiSearchListener? = null
  private var listener: OnViewActionListener? = null
  private var stickersInfoWindowAdapter: StickersInfoWindowAdapter? = null

  private var currentLocation: LatLonPoint? = null
  private var mapClickFlag = false

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val layout = inflater.inflate(R.layout.fragment_home_page, container, false)
    mapView = layout.findViewById(R.id.mv_map)
    drawView = layout.findViewById(R.id.draw_view)
    zoomLevel = layout.findViewById(R.id.tv_zoom_level)
    locationShadow = layout.findViewById(R.id.location_shadow)
    controlShadow = layout.findViewById(R.id.control_shadow)
    rlSearch = layout.findViewById(R.id.rl_search)
    ibControl = layout.findViewById(R.id.ib_control)
    ivLocation = layout.findViewById(R.id.iv_location)
    keywordsSearch = layout.findViewById(R.id.keywords_search)
    cleanKeyWords = layout.findViewById(R.id.clean_keywords)

    drawView?.setMapView(mapView)

    toolsView = layout.findViewById(R.id.tools_view)
    toolsView?.setDrawView(drawView)
    toolsView?.bindData()
    toolsView.setMargins(marginTop = BaseActivity.STATUS_BAR_HEIGHT)

    locationShadow?.let {
      ShadowDrawable.Builder()
        .setShadowColor(getApplication().resources.getColor(R.color.color_24000000))
        .setShadowRadius(TUtil.dp2px(20f))
        .setOffsetY(TUtil.dp2px(2f))
        .into(it)
    }
    controlShadow?.let {
      ShadowDrawable.Builder()
        .setShadowColor(getApplication().resources.getColor(R.color.color_24000000))
        .setShadowRadius(TUtil.dp2px(20f))
        .setOffsetY(TUtil.dp2px(2f))
        .into(it)
    }
    return layout
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mapView?.onCreate(savedInstanceState)

    checkLocationPermission()

    ibControl?.setOnClickListener(this)
    keywordsSearch?.let {
      setSpannable(it)
      it.setOnClickListener(this@HomePageFragment)
    }
    cleanKeyWords?.setOnClickListener(this)

    // 初始化地图控制器对象
    initMap()
  }

  private fun initMap() {
    if (aMap == null) {
      aMap = mapView?.map?.apply {
        setOnCameraChangeListener(this@HomePageFragment)
        setOnMarkerClickListener(this@HomePageFragment)
        setOnMapClickListener(this@HomePageFragment)
//        setInfoWindowAdapter(MarkerInfoWindowAdapter(this@HomePageFragment))
      }
      poiSearchListener = PoiSearchListener(context, aMap)
      mapLocationListener = MapLocationListener(activity, aMap)

      // 首次进入页面时允许自动定位
      if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        mapLocationListener?.initLocation()
        ivLocation.visible()
        mapLocationListener?.setCustomLocation(ivLocation)
      } else {
        ivLocation.gone()
      }
    }
  }

  private fun setSpannable(textView: TextView) {
    val sp = SpannableString("   " + textView.hint)

    val drawable = resources.getDrawable(R.drawable.icon_search)
    drawable.setBounds(0, 0, TUtil.dp2px(16f), TUtil.dp2px(16f))
    val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BASELINE)

    sp.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    textView.text = sp
  }

  override fun onCameraChange(position: CameraPosition?) {
    zoomLevel?.apply {
      text = "缩放级别：" + position?.zoom.toString()
    }
    toolsView?.controlStickers(position?.zoom)
    drawView?.polylineVisibleOrGone()
    stickersInfoWindowAdapter?.controlIconMarker(position?.zoom)
  }

  override fun onCameraChangeFinish(position: CameraPosition?) {}

  override fun onDestroy() {
    super.onDestroy()
    mapView?.onDestroy()
    if (mapLocationListener?.getLocationClient() != null) {
      mapLocationListener?.getLocationClient()?.onDestroy()
    }
    mapLocationListener?.triggerDestroy()
  }

  override fun onResume() {
    super.onResume()
    mapView?.onResume()
  }

  private fun checkLocationPermission() {
    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
    }
  }

  override fun onPause() {
    super.onPause()
    mapView?.onPause()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView?.onSaveInstanceState(outState)
  }

  // 定位权限请求结果回调
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      mapLocationListener?.initLocation()
      ivLocation.visible()
      mapLocationListener?.setCustomLocation(ivLocation)
      Toast.makeText(context, "Location permission granted", Toast.LENGTH_SHORT).show()
    } else {
      Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
      showPermissionExplanationDialog()
      ivLocation.gone()
    }
  }

  // 显示权限说明对话框
  private fun showPermissionExplanationDialog() {
    if (activity?.isFinishing == false && activity?.isDestroyed == false) {
      try {
        AlertDialog.Builder(activity)
          .setTitle("需要定位权限")
          .setMessage("应用需要定位权限以提供地图功能，请在设置中开启权限。")
          .setPositiveButton("去设置") { _, _ ->
            // 打开应用设置页面
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
          }
          .setNegativeButton("取消", null)
          .show()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  override fun onMarkerClick(marker: Marker?): Boolean {
    marker?.showInfoWindow()
    return false
  }

  override fun onMapClick(latlng: LatLng?) {
    if (latlng == null) return
    mapClickFlag = !mapClickFlag

    if (mapClickFlag) {
      mStickerMarker?.remove()
      val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.marker)
      if (drawable != null) {
        // 将 Drawable 转换为 Bitmap
        var bitmap = drawable.toBitmap()
        // 缩放 Bitmap
        bitmap = bitmap.scaleBitmap(40, 40)
        // 将缩放后的 Bitmap 转换为 BitmapDescriptor
        val icon: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)

        mStickerMarker = aMap?.addMarker(MarkerOptions()
          .position(latlng)
          .icon(icon))
      }
      // 设置信息窗口适配器
      stickersInfoWindowAdapter = StickersInfoWindowAdapter(this@HomePageFragment, aMap, latlng)
      aMap?.setInfoWindowAdapter(stickersInfoWindowAdapter)
      
      // 显示信息窗口
      mStickerMarker?.showInfoWindow()
      toolsView?.setMarker(mStickerMarker)
    } else {
      mStickerMarker?.remove()
      aMap?.setInfoWindowAdapter(null)
    }
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.ib_control -> {
        ibControl?.isSelected = !ibControl?.isSelected!!
        if (ibControl?.isSelected == true) {
          rlSearch?.gone()
          listener?.onButtonClicked(true)
        } else {
          rlSearch?.visible()
          listener?.onButtonClicked(false)
        }
      }

      R.id.keywords_search -> {
        val intent = Intent(context, SearchActivity::class.java)
        currentLocation = TUtil.convertToLatLonPoint(mapLocationListener?.getCurrentLocation())
        intent.putExtra(Constants.EXTRA_LATLNG, currentLocation)
        startActivityForResult(intent, Constants.REQUEST_CODE)
      }

      R.id.clean_keywords -> {
        keywordsSearch?.text = ""
        clearMarkers()
        cleanKeyWords?.gone()
      }

      else -> {}
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Constants.RESULT_CODE_INPUTTIPS && data != null) {
      clearMarkers()
      val tip = data.getParcelableExtra<Tip>(Constants.EXTRA_TIP)
      if (tip?.poiID.isNullOrEmpty()) {
        poiSearchListener?.doSearchQuery(tip?.name, currentLocation)
      } else {
        addTipMarker(tip)
      }
      keywordsSearch?.text = tip?.name
      if (tip?.name != "") {
        cleanKeyWords?.visible()
      }
    } else if (resultCode == Constants.RESULT_CODE_KEYWORDS && data != null) {
      clearMarkers()
      val keywords = data.getStringExtra(Constants.KEY_WORDS_NAME)
      if (keywords != null && keywords != "") {
        poiSearchListener?.doSearchQuery(keywords, currentLocation)
      }
      keywordsSearch?.text = keywords
      if (keywords != "") {
        cleanKeyWords?.visible()
      }
    }
  }

  /**
   * 用marker展示输入提示list选中数据
   *@param tip
   */
  private fun addTipMarker(tip: Tip?) {
    if (tip == null) return
    mPoiMarker = aMap?.addMarker(MarkerOptions())
    val point = tip.point
    if (point != null) {
      val markerPosition = LatLng(point.latitude, point.longitude)
      mPoiMarker?.position = markerPosition
      aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17f), 1000, null)
    }
    mPoiMarker?.title = tip.name
    mPoiMarker?.snippet = tip.address
  }

  private fun clearMarkers() {
    mPoiMarker?.remove()
    mPoiMarker = null
    poiSearchListener?.clearPoiOverlay()
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is OnViewActionListener) {
      listener = context
    }
  }

  interface OnViewActionListener {
    fun onButtonClicked(isSelected: Boolean)
  }
}