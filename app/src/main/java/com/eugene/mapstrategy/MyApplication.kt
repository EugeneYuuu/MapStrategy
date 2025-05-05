package com.eugene.mapstrategy

import android.app.Application
import com.tencent.mmkv.MMKV

class MyApplication : Application() {

  companion object {
    @Volatile
    private var instance: MyApplication? = null

    @JvmStatic
    fun getApplication(): MyApplication {
      return instance ?: throw IllegalStateException("Application is not initialized")
    }
  }

  override fun onCreate() {
    super.onCreate()

    instance = this
    MMKV.initialize(this)
  }
}