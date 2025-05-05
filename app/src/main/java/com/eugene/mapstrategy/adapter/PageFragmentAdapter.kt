package com.eugene.mapstrategy.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eugene.mapstrategy.fragment.BaseFragment

/**
 * @author EugeneYu
 * @date 2025/3/7
 * @desc ViewPager2适配器。创建FragmentStateAdapter，动态添加数据，更新适配器，使用TabLayoutMediator绑定，并在数据变化时刷新
 */
class PageFragmentAdapter: FragmentStateAdapter {

  constructor(activity: FragmentActivity): super(activity)
  constructor(fragment: Fragment) : super(fragment)

  private val fragments = mutableListOf<BaseFragment>()

  fun addFragment(fragment: BaseFragment) {
    fragments.add(fragment)
    notifyItemInserted(itemCount - 1)
  }
  
  override fun getItemCount(): Int {
    return fragments.size
  }

  override fun createFragment(position: Int): Fragment {
    return fragments[position]
  }

}