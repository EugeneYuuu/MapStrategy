package com.eugene.mapstrategy.adapter

import com.amap.api.maps.model.LatLng
import com.eugene.mapstrategy.bean.PolylineData
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * @author EugeneYu
 * @date 2024/12/19
 * @desc Polyline序列化和反序列化
 */
class PolylineTypeAdapter : TypeAdapter<PolylineData>() {
  override fun write(out: JsonWriter, value: PolylineData) {
    out.beginObject()
      .name("points").beginArray()
    value.points?.forEach { point ->
      val latLnAdapter = LatLngTypeAdapter()
      latLnAdapter.write(out, point)
    }
    out.endArray()
      .name("width").value(value.width)
      .name("color").value(value.color)
      .endObject()
  }

  override fun read(`in`: JsonReader): PolylineData {
    val points = mutableListOf<LatLng>()
    var width = 0f
    var color = 0
    `in`.beginObject()
    while (`in`.hasNext()) {
      when (`in`.nextName()) {
        "points" -> {
          `in`.beginArray()
          val latLngAdapter = LatLngTypeAdapter()
          while (`in`.hasNext()) {
            points.add(latLngAdapter.read(`in`))
          }
          `in`.endArray()
        }
        "width" -> width = `in`.nextDouble().toFloat()
        "color" -> color = `in`.nextInt()
      }
    }
    `in`.endObject()
    return PolylineData(points, width, color)
  }
}

class LatLngTypeAdapter : TypeAdapter<LatLng>() {
  override fun write(out: JsonWriter, value: LatLng) {
    out.beginObject()
      .name("lat").value(value.latitude)
      .name("lng").value(value.longitude)
      .endObject()
  }

  override fun read(`in`: JsonReader): LatLng {
    var lat = 1.0
    var lng = 1.0
    `in`.beginObject()
    while (`in`.hasNext()) {
      when (`in`.nextName()) {
        "lat" -> lat = `in`.nextDouble()
        "lng" -> lng = `in`.nextDouble()
      }
    }
    `in`.endObject()
    return LatLng(lat, lng)
  }
}