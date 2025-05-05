package com.eugene.mapstrategy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.activity.BaseActivity
import com.eugene.mapstrategy.utils.setMargins

class ProfileFragment: BaseFragment() {

  private  var ivAvatar: ImageView? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val layout =  inflater.inflate(R.layout.fragment_profile, container, false)
    ivAvatar = layout.findViewById(R.id.iv_avatar)
    return layout
  }

}