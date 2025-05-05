package com.eugene.mapstrategy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.amap.api.services.help.Tip
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.view.InputTipsCard

/**
 * @author EugeneYu
 * @date 2025/3/21
 * @desc 输入提示adapter，展示item名称和地址
 */
class InputTipsAdapter(
  private val mContext: Context,
  private val mListTips: List<Tip>,
  private val block: ((tip: Tip) -> Unit)?)
  : RecyclerView.Adapter<ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    var view = LayoutInflater.from(mContext).inflate(R.layout.card_input_tips, parent, false)
    return InputTipsCard(view)

  }

  override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
    val tip = mListTips[position]
    if (viewHolder is InputTipsCard) {
      viewHolder.bindData(tip) { data ->
        block?.invoke(data)
      }
    }

  }

  override fun getItemCount(): Int {
    return mListTips.size
  }
}
