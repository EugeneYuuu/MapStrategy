package com.eugene.mapstrategy.listener

import android.graphics.Bitmap

interface ImageLoadListener {
  /**
   * 图片加载成功回调
   *
   * @param bitmap 加载成功的Bitmap
   */
  fun onLoadSuccess(bitmap: Bitmap?)

  /**
   * 图片加载失败回调
   *
   * @param exception 异常信息
   */
  fun onLoadFailed(exception: Exception)
}