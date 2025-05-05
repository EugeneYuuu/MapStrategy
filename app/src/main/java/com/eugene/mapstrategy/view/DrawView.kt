package com.eugene.mapstrategy.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap.SQUARE
import android.graphics.Paint.Join
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.graphics.Point
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.compose.ui.geometry.times
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.MapView
import com.amap.api.maps.Projection
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Polyline
import com.amap.api.maps.model.PolylineOptions
import com.eugene.mapstrategy.adapter.LatLngTypeAdapter
import com.eugene.mapstrategy.adapter.PolylineTypeAdapter
import com.eugene.mapstrategy.bean.PolylineData
import com.eugene.mapstrategy.utils.CatmullRom
import com.eugene.mapstrategy.utils.Constants.LARGE_SCALE
import com.eugene.mapstrategy.utils.Constants.MIDDLE_SCALE
import com.eugene.mapstrategy.utils.Constants.SMALL_SCALE
import com.eugene.mapstrategy.utils.TUtil
import com.eugene.mapstrategy.utils.gones
import com.eugene.mapstrategy.utils.isVisible
import com.eugene.mapstrategy.utils.toColor
import com.eugene.mapstrategy.utils.visibles
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import kotlin.math.pow

/**
 * @author EugeneYu
 * @date 2024/12/19
 * @desc 绘制view
 */
class DrawView @JvmOverloads constructor(
  context: Context?,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

  private var selectPath: Path? = null
  private val paintPaths: MutableList<Path?> = ArrayList() // 路径集合
  private val paints: MutableList<Paint?> = ArrayList() // 画笔集合
  private val allPoints: MutableList<Point?> = ArrayList() // 单次线条的点集合

  private var paint: Paint? = null

  private var mapView: MapView? = null
  private var aMap: AMap? = null
  //不同缩放层级的涂鸦集合
  private val smallScalePolylines: MutableList<Polyline> = ArrayList()
  private val middleScalePolylines: MutableList<Polyline> = ArrayList()
  private val largeScalePolylines: MutableList<Polyline> = ArrayList()
  private val allScalePolylines: MutableList<Polyline> = ArrayList()

  private val mmkv: MMKV? by lazy {
    MMKV.mmkvWithID("drawn_lines", MMKV.MULTI_PROCESS_MODE)
  }
  private var handlerThread: HandlerThread? = null // 改为可空变量，在每次附加窗口时都创建新的HandlerThread实例
  private var subHandler: Handler? = null

  private var isEraserMode: Boolean = false

  init {
    paint = Paint().apply {
      this.color = "#FF000000".toColor()
      isAntiAlias = true
      style = STROKE
      strokeCap = SQUARE
      strokeJoin = Join.ROUND
      strokeWidth = 5f
    }
    paints.add(Paint(paint))
    initPath()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.save()
    for (i in paintPaths.indices) {
      canvas.drawPath(paintPaths[i] ?: Path(), paints[i] ?: Paint())
    }
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    val x = event.x
    val y = event.y

    if (isEraserMode) {
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
          erasePolylineAt(x, y)
        }
      }
      return true
    }

    allPoints.add(Point(x.toInt(), y.toInt()))

    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        selectPath?.moveTo(x, y)
      }

      MotionEvent.ACTION_MOVE -> {
        if (event.pointerCount == 1) {
          selectPath?.lineTo(x, y)
        }
      }

      MotionEvent.ACTION_UP -> {
        val geoPoints = TUtil.convertPointToLatLng(aMap?.projection, allPoints)
        allPoints.clear()

        aMap?.let { amap ->
          val catmullPoints = CatmullRom.createSmoothCurve(geoPoints, 10)

          val zoom = amap.cameraPosition.zoom

          //根据地图缩放级别判断使用哪种类型的涂鸦
          amap.addPolyline(
            PolylineOptions().addAll(catmullPoints).width(paint?.strokeWidth ?: 5f).color(paint?.color ?: Color.BLACK)
          ) ?.let {
            if (zoom >= SMALL_SCALE) smallScalePolylines.add(it)
            else if (zoom < SMALL_SCALE && zoom >= MIDDLE_SCALE) middleScalePolylines.add(it)
            else if (zoom < MIDDLE_SCALE && zoom >= LARGE_SCALE) largeScalePolylines.add(it)
            else allScalePolylines.add(it)
            it.zIndex = 10f
          }
        }

        saveLines()
      }
    }
    invalidate()
    return true
  }

  fun clearBoard() {
    for (i in paintPaths.indices) {
      paintPaths[i]?.reset()
    }
    invalidate()
  }

  private fun initPath() {
    val path = Path()
    paintPaths.add(path)
    selectPath = path
  }

  fun setPaintColor(color: Int) {
    paint?.color = color
    paints.add(Paint(paint))
    initPath()
  }

  fun setPaintStrokeWidth(strokeWidth: Float) {
    paint?.strokeWidth = strokeWidth
    paints.add(Paint(paint))
    initPath()
  }

  fun setMapView(mapView: MapView?) {
    this.mapView = mapView
    this.aMap = mapView?.map
  }

  fun polylineVisibleOrGone() {
    val zoom = aMap?.cameraPosition?.zoom ?: return
    subHandler?.post {
      if (zoom >= SMALL_SCALE) {
        smallScalePolylines.visibles()
      } else {
        smallScalePolylines.gones()
      }

      if (zoom in MIDDLE_SCALE..SMALL_SCALE) {
        middleScalePolylines.visibles()
      } else {
        middleScalePolylines.gones()
      }

      if (zoom in LARGE_SCALE..MIDDLE_SCALE) {
        largeScalePolylines.visibles()
      } else {
        largeScalePolylines.gones()
      }

      if (zoom < LARGE_SCALE) {
        allScalePolylines.visibles()
      } else {
        allScalePolylines.gones()
      }
    }
  }

  /**
   * 保存polylines
   */
  private fun saveLines() {
    subHandler?.post {
      val gson = GsonBuilder()
        .registerTypeAdapter(PolylineData::class.java, PolylineTypeAdapter())
        .registerTypeAdapter(LatLng::class.java, LatLngTypeAdapter())
        .serializeNulls()
        .create()

      val dataMap = mapOf(
        "smallScale" to smallScalePolylines.map {
          PolylineData(it.points, it.width, it.color)
        },
        "middleScale" to middleScalePolylines.map {
          PolylineData(it.points, it.width, it.color)
        },
        "largeScale" to largeScalePolylines.map {
          PolylineData(it.points, it.width, it.color)
        },
        "allScale" to allScalePolylines.map {
          PolylineData(it.points, it.width, it.color)
        }
      )

      val json = gson.toJson(dataMap)
      mmkv?.encode("polylines", json)
    }
  }

  /**
   * 加载polylines
   */
  private fun loadLines() {
    subHandler?.post {
      val gson = GsonBuilder()
        .registerTypeAdapter(PolylineData::class.java, PolylineTypeAdapter())
        .registerTypeAdapter(LatLng::class.java, LatLngTypeAdapter()) // 注册LatLngTypeAdapter
        .serializeNulls()
        .create()

      val json = mmkv?.decodeString("polylines")

      if (!json.isNullOrEmpty()) {
        val type = object : TypeToken<Map<String, List<PolylineData>>>() {}.type
        try {
          val dataMap: Map<String, List<PolylineData>> = gson.fromJson(json, type)
          dataMap["smallScale"]?.forEach { addPolyline(it, smallScalePolylines) }
          dataMap["middleScale"]?.forEach { addPolyline(it, middleScalePolylines) }
          dataMap["largeScale"]?.forEach { addPolyline(it, largeScalePolylines) }
          dataMap["allScale"]?.forEach { addPolyline(it, allScalePolylines) }
        } catch (e: Exception) {
          Log.e("loadLines", "Error parsing data: ${e.message}")
        }
      }
    }
  }

  private fun erasePolylineAt(x: Float, y: Float) {
    val projection = aMap?.projection ?: return
    val touchPoint = projection.fromScreenLocation(Point(x.toInt(), y.toInt()))
    // 动态计算擦除半径：固定像素半径 × 当前每像素代表的米数
    val pixelRadius = 50f // 可调整的屏幕像素半径
    val metersPerPixel = TUtil.getMetersPerPixel(projection, width, height)
    val currentEraserRadius = pixelRadius * metersPerPixel

    touchPoint?.let { center ->
      // 处理所有四个列表
      val allPolylines = listOf(
        smallScalePolylines,
        middleScalePolylines,
        largeScalePolylines,
        allScalePolylines
      )
      for (polylineList in allPolylines) {
        if (!polylineList.isVisible()) continue
        val iterator = polylineList.iterator()
        while (iterator.hasNext()) {
          val polyline = iterator.next()
          var shouldRemove = false

          val points = polyline.points
          for (i in 0 until points.size - 1) {
            val pointA = points[i]
            val pointB = points[i + 1]

            if (TUtil.isLineIntersectingCircle(pointA, pointB, center, currentEraserRadius)) {
              shouldRemove = true
              break
            }
          }

          if (shouldRemove) {
            polyline.remove()
            iterator.remove()
            break
          }
        }
      }
      saveLines()
    }
  }

  fun setEraserMode(enabled: Boolean) {
    isEraserMode = enabled
  }

  private fun addPolyline(data: PolylineData, targetList: MutableList<Polyline>) {
    aMap?.addPolyline(
      PolylineOptions()
        .addAll(data.points)
        .width(data.width ?: 5f)
        .color(data.color ?: Color.BLACK)
    )?.let {
      targetList.add(it)
      it.zIndex = 10f //调整显示层级，确保层级高于地图标签
    }
  }

  private fun clearAllPolylines() {
    val allPolylines = listOf(
      smallScalePolylines,
      middleScalePolylines,
      largeScalePolylines,
      allScalePolylines
    )
    allPolylines.forEach { list ->
      list.forEach { it.remove() }
      list.clear()
    }
  }

  /**
   * 问题：切换Fragment会导致多次调用onAttachedToWindow时，可能会尝试多次启动同一个线程；
   *          因为HandlerThread一旦被启动后，再次调用 start() 会抛出IllegalThreadStateException
   * 解决：在每次附加到窗口时创建新的线程实例，并在分离时释放资源。确保线程未存活时创建新实例
   */
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (handlerThread?.isAlive != true) {
      handlerThread?.quitSafely() // 清理旧线程
      handlerThread = HandlerThread("drawViewThread").apply {
        start()
        subHandler = Handler(looper)
      }
    }
    //onAttachedToWindow会在onResume之后调用，放在onResume会导致子线程还没有创建就调用了loadLines
    //所以在这里加载数据
    loadLines()
    polylineVisibleOrGone()
  }

  override fun onDetachedFromWindow() {
    clearAllPolylines()
    // 释放资源
    subHandler?.removeCallbacksAndMessages(null)
    handlerThread?.quitSafely()
    handlerThread = null
    super.onDetachedFromWindow()
  }
}