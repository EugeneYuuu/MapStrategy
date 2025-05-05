package com.eugene.mapstrategy.bean

import androidx.annotation.DrawableRes

data class MSTabItem(
  val title: String,
  @DrawableRes val unselectIcon: Int,
  @DrawableRes val selectedIcon: Int
)