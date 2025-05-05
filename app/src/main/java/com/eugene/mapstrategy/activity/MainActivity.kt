package com.eugene.mapstrategy.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.amap.api.location.AMapLocationClient.updatePrivacyAgree
import com.amap.api.location.AMapLocationClient.updatePrivacyShow
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.adapter.PageFragmentAdapter
import com.eugene.mapstrategy.bean.MSTabItem
import com.eugene.mapstrategy.fragment.HomePageFragment
import com.eugene.mapstrategy.fragment.HomePageFragment.OnViewActionListener
import com.eugene.mapstrategy.fragment.ProfileFragment
import com.eugene.mapstrategy.utils.gone
import com.eugene.mapstrategy.utils.toColor
import com.eugene.mapstrategy.utils.visible
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @author EugeneYu
 * @date 2024/12/18
 * @desc
 */
class MainActivity : BaseActivity(), OnViewActionListener {

  private var pageAdapter: PageFragmentAdapter? = null
  private var tabLayout: TabLayout? = null
  private var viewPager: ViewPager2? = null
  private val tabItems = listOf(
    MSTabItem("首页", R.drawable.homepage_unselect, R.drawable.homepage_selected),
    MSTabItem("我的", R.drawable.profile_unselect, R.drawable.profile_selected)
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    updatePrivacyShow(this, true, true)
    updatePrivacyAgree(this, true)
    setContentView(R.layout.activity_main)

    initView()

    initTabLayout()
  }

  private fun initView() {
    //初始化viewPage2适配器
    pageAdapter = PageFragmentAdapter(this).apply {
      addFragment(HomePageFragment())
      addFragment(ProfileFragment())
    }
    tabLayout = findViewById(R.id.tab_layout)
    viewPager = findViewById(R.id.view_pager)
    viewPager?.adapter = pageAdapter
    viewPager?.isUserInputEnabled = false
  }


  private fun initTabLayout() {
    TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
      val tabView = LayoutInflater.from(this).inflate(R.layout.tab_item, null)
      val tabText = tabView.findViewById<TextView>(R.id.tab_text).apply {
        text = tabItems[position].title
      }
      val tabImage = tabView.findViewById<ImageView>(R.id.tab_icon)
      // 初始状态
      if (position == 0) {
        tabImage.setImageResource(tabItems[position].selectedIcon)
        tabText.setTextColor("#1296db".toColor())
        tabText.isSelected = true
      } else {
        tabImage.setImageResource(tabItems[position].unselectIcon)
        tabText.setTextColor("#000000".toColor())
        tabText.isSelected = false
      }

      tab.customView = tabView
    }.attach()

    tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
      override fun onTabSelected(tab: TabLayout.Tab) {
        updateTabAppearance(tab, true)
      }

      override fun onTabUnselected(tab: TabLayout.Tab) {
        updateTabAppearance(tab, false)
      }

      override fun onTabReselected(tab: TabLayout.Tab) {}
    })
  }

  private fun updateTabAppearance(tab: TabLayout.Tab, isSelected: Boolean) {
    val position = tab.position
    val customView = tab.customView ?: return
    val icon = customView.findViewById<ImageView>(R.id.tab_icon)
    val text = customView.findViewById<TextView>(R.id.tab_text)

    text.isSelected = isSelected
    text.setTextColor(
      if (isSelected) "#1296db".toColor() else "#000000".toColor()
    )
    icon.setImageResource(
      if (isSelected) tabItems[position].selectedIcon else tabItems[position].unselectIcon
    )
  }

  override fun onButtonClicked(isSelected: Boolean) {
    if (isSelected) {
      tabLayout?.gone()
    } else {
      tabLayout?.visible()
    }
  }
}