package com.eugene.mapstrategy.adapter

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.amap.api.maps.AMap.InfoWindowAdapter
import com.amap.api.maps.model.Marker
import com.eugene.mapstrategy.R


/**
 * @author EugeneYu
 * @date 2025/3/19
 * @desc 设置自定义的点标记样式
 */
class MarkerInfoWindowAdapter(fragment: Fragment): InfoWindowAdapter {
    private var mFragment = fragment
    override fun getInfoWindow(marker: Marker?): View {
        val view: View = mFragment.layoutInflater.inflate(R.layout.poikeywordsearch_uri, null)
        val title = view.findViewById<View>(R.id.title) as TextView
        title.text = marker?.title

        val snippet = view.findViewById<View>(R.id.snippet) as TextView
        snippet.text = marker?.snippet
        return view
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }
}