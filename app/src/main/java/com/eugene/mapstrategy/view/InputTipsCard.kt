package com.eugene.mapstrategy.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.help.Tip
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.utils.gone
import com.eugene.mapstrategy.utils.visible

/**
 * @author EugeneYu
 * @date 2025/3/21
 * @desc
 */
class InputTipsCard(itemView: View): RecyclerView.ViewHolder(itemView) {

  private val tvName: TextView
  private val tvAddress: TextView

  init {
    tvName = itemView.findViewById(R.id.name)
    tvAddress = itemView.findViewById(R.id.address)
  }


  fun bindData(data: Tip?, block: ((data: Tip) -> Unit)?) {
    if (data == null) return

    itemView.setOnClickListener {
      block?.invoke(data)
    }

    if (data.name.isNullOrBlank()) {
      tvName.gone()
    } else {
      tvName.visible()
      tvName.text = data.name
    }

    if (data.address.isNullOrBlank()) {
      tvAddress.gone()
    } else {
      tvAddress.visible()
      tvAddress.text = data.address
    }
  }
}