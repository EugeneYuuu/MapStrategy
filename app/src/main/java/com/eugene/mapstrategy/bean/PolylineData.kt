package com.eugene.mapstrategy.bean

import com.amap.api.maps.model.LatLng

data class PolylineData(
    val points: List<LatLng>? = null,
    val width: Float? = null,
    val color: Int? = null
)