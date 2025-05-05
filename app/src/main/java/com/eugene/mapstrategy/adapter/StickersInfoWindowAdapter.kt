package com.eugene.mapstrategy.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.InfoWindowAdapter
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.fragment.StickersFragment
import com.eugene.mapstrategy.utils.TUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @author EugeneYu
 * @date 2025/3/19
 * @desc 设置自定义的贴纸弹窗样式
 */
class StickersInfoWindowAdapter(
    private val fragment: Fragment,
    private val aMap: AMap?,
    private var latlng: LatLng?
): InfoWindowAdapter {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager2? = null
    private var iconMarkers: MutableList<Marker?> = ArrayList()

    override fun getInfoWindow(marker: Marker?): View {
        val view: View = fragment.layoutInflater.inflate(R.layout.stickers_info_window, null)
        bindInfoWindowView(view, marker)
        initTabLayout()
        return view
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    private fun bindInfoWindowView(view: View, marker: Marker?) {
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        val pageAdapter = PageFragmentAdapter(fragment)
        pageAdapter.addFragment(StickersFragment { img ->
            // 设置图片点击监听
            addCustomMarker(latlng, img)
            marker?.hideInfoWindow()
        })
        viewPager?.adapter = pageAdapter
        viewPager?.isUserInputEnabled = false
    }

    private fun initTabLayout() {
        TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            tab.customView = ImageView(fragment.context).apply {
                Glide.with(context)
                    .load("https://img.picui.cn/free/2025/04/30/6811cd56cda69.png")
                    .transform(RoundedCorners(TUtil.dp2px(6f)))
                    .into(this)
                scaleType = ImageView.ScaleType.FIT_CENTER
                layoutParams = ViewGroup.LayoutParams(TUtil.dp2px(25f), TUtil.dp2px(25f))
            }
        }.attach()
    }

    // 添加自定义标记
    private fun addCustomMarker(position: LatLng?, img: String) {
        position ?: return

        Glide.with(fragment.requireContext())
            .asBitmap()
            .load(img)
            .override(100, 100) // 设置图片大小
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val icon = BitmapDescriptorFactory.fromBitmap(resource)
                    val options = MarkerOptions()
                        .position(position)
                        .icon(icon)

                    iconMarkers.add(aMap?.addMarker(options))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // 加载失败时使用默认图标
                    val options = MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.defaultMarker())

                    iconMarkers.add(aMap?.addMarker(options))
                }
            })
    }

    fun controlIconMarker(zoom: Float?) {
        zoom?.let {
            if (zoom < 14) {
                iconMarkers.all {
                    it?.isVisible = false
                    true
                }
            } else {
                iconMarkers.all {
                    it?.isVisible = true
                    true
                }
            }
        }
    }
}