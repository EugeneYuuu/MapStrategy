package com.eugene.mapstrategy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.view.StickersCard

class StickersAdapter(
  private val mContext: Context?,
  private val mListStickers: List<String>?,
  private val block: (img: String) -> Unit
): RecyclerView.Adapter<ViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, data: Int): ViewHolder {
    var view = LayoutInflater.from(mContext).inflate(R.layout.card_stickers, parent, false)
    return StickersCard(view)
  }

  override fun getItemCount(): Int {
    if (mListStickers == null) return 0
    return mListStickers.size
  }

  override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
    val data = mListStickers?.get(position)
    if (viewHolder is StickersCard) {
      viewHolder.bindData(data, position+1, block)
    }
  }
}