package com.eugene.mapstrategy.view

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.utils.TUtil
import com.eugene.mapstrategy.utils.setMargins

class StickersCard(itemView: View): ViewHolder(itemView) {

  fun bindData(
    img: String?,
    index: Int,
    block: (image: String) -> Unit
  ) {
    if (img == null) return
    itemView.findViewById<ImageView>(R.id.sticker_img).apply {
      Glide.with(context)
        .load(img)
        .transform(RoundedCorners(TUtil.dp2px(6f)))
        .into(this)
      setMargins(
        marginTop = if (index != 1) TUtil.dp2px(4f) else TUtil.dp2px(0f),
        marginEnd =  if (index % 4 != 0) TUtil.dp2px(4f) else TUtil.dp2px(0f)
      )

      setOnClickListener { block.invoke(img) }
    }
  }
}