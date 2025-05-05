package com.eugene.mapstrategy.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.eugene.mapstrategy.utils.TUtil
import com.eugene.mapstrategy.utils.setMargins

open class BaseActivity : AppCompatActivity() {

  // 根布局的引用
  protected lateinit var rootLayout: ViewGroup
  private set

  companion object {
    var STATUS_BAR_HEIGHT = 0
  }

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStatusInfo()
    STATUS_BAR_HEIGHT = TUtil.getStatusBarHeight(this)
  }

  /**
   * 重写 setContentView 以捕获根布局
   * 在子Activity设置布局后自动初始化 rootLayout
   */
  override fun setContentView(layoutResID: Int) {
    super.setContentView(layoutResID)
    initRootLayout()
  }

  override fun setContentView(view: View?) {
    super.setContentView(view)
    initRootLayout()
  }

  override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
    super.setContentView(view, params)
    initRootLayout()
  }

  private fun initRootLayout() {
    // 使用 android.R.id.content 获取内容根视图，适用于所有 Activity
    rootLayout = findViewById(android.R.id.content)
    setupNavigationBarMargin()
  }

  private fun setupNavigationBarMargin() {
    ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
      val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
      // 动态设置底部 padding
      view.setMargins(marginBottom = navigationBarHeight)
      insets
    }
  }

  private fun setStatusInfo() {
    window.statusBarColor = Color.TRANSPARENT
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // 设置浅色状态栏文字（深色图标）
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
  }
}