package com.eugene.mapstrategy.listener

import android.app.ProgressDialog
import android.content.Context
import com.amap.api.maps.AMap
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.core.SuggestionCity
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener
import com.amap.api.services.poisearch.PoiSearch.Query
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.overlay.PoiOverlay
import com.eugene.mapstrategy.utils.ToastUtil

/**
 * @author EugeneYu
 * @date 2025/3/19
 * @desc Poi搜索类
 */
class PoiSearchListener(context: Context?, private val aMap: AMap?): OnPoiSearchListener {
  private val mContext = context
  private val mKeyWords = "" // 要输入的poi搜索关键字
  private var progDialog: ProgressDialog? = null // 搜索时进度条

  private var poiResult: PoiResult? = null // poi返回的结果
  private var currentPage = 1
  private var query: Query? = null // Poi查询条件类
  private var poiSearch: PoiSearch? = null // POI搜索

  private var poiOverlay: PoiOverlay? = null // poi图层

  /**
   * 显示进度框
   */
  private fun showProgressDialog() {
    if (progDialog == null) {
      progDialog = ProgressDialog(mContext).apply {
        setProgressStyle(ProgressDialog.STYLE_SPINNER)
        isIndeterminate = false
        setCancelable(false)
        setMessage("正在搜索:\n$mKeyWords")
        show()
      }
    }
  }

  /**
   * 隐藏进度框
   */
  private fun dissmissProgressDialog() {
    if (progDialog != null) {
      progDialog?.dismiss()
    }
  }

  /**
   * poi没有搜索到数据，返回一些推荐城市的信息
   */
  private fun showSuggestCity(cities: List<SuggestionCity>) {
    var infomation = "推荐城市\n"
    for (i in cities.indices) {
      infomation += ("城市名称:" + cities[i].cityName + "城市区号:"
          + cities[i].cityCode + "城市编码:"
          + cities[i].adCode + "\n")
    }
    ToastUtil.show(mContext, infomation)
  }

  /**
   * 开始进行poi搜索
   */
  fun doSearchQuery(keywords: String?, currentLocation: LatLonPoint?) {
    showProgressDialog() // 显示进度框
    currentPage = 1
    // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
    query = Query(keywords, "", null).apply {
      location = currentLocation
      cityLimit = false
    }
    // 设置每页最多返回多少条poiitem
    query?.pageSize = 10
    // 设置查第一页
    query?.pageNum = currentPage

    poiSearch = PoiSearch(mContext, query)
    poiSearch?.setOnPoiSearchListener(this)
    poiSearch?.searchPOIAsyn()
  }

  override fun onPoiSearched(result: PoiResult?, rCode: Int) {
    dissmissProgressDialog() // 隐藏对话框
    if (rCode == 1000) {
      if (result?.query != null) { // 搜索poi的结果
        if (result.query.equals(query)) { // 是否是同一条
          poiResult = result
          // 取得搜索到的poiitems有多少页
          val poiItems: ArrayList<PoiItem>? = poiResult?.pois // 取得第一页的poiitem数据，页数从数字0开始
          val suggestionCities: List<SuggestionCity> = poiResult!!.searchSuggestionCitys // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

          if (poiItems != null && poiItems.size > 0) {
            poiOverlay = PoiOverlay(aMap, poiItems).apply {
              removeFromMap()
              addToMap()
              zoomToSpan()
            }
          } else if (suggestionCities.isNotEmpty()) {
            showSuggestCity(suggestionCities)
          } else {
            ToastUtil.show(
              mContext,
              R.string.no_result
            )
          }
        }
      } else {
        ToastUtil.show(
          mContext,
          R.string.no_result
        )
      }
    } else {
      ToastUtil.showError(mContext, rCode)
    }
  }

  override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {}

  fun clearPoiOverlay() {
    poiOverlay?.removeFromMap()
    poiOverlay = null
  }
}