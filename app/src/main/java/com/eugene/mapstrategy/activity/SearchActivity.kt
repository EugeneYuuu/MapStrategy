package com.eugene.mapstrategy.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.Inputtips.InputtipsListener
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.adapter.InputTipsAdapter
import com.eugene.mapstrategy.utils.Constants
import com.eugene.mapstrategy.utils.ToastUtil
import com.eugene.mapstrategy.utils.setMargins

/**
 * @author EugeneYu
 * @date 2025/3/21
 * @desc 搜索提示页
 */
class SearchActivity: BaseActivity(), OnQueryTextListener,
  InputtipsListener, OnClickListener{
    private var llSearchView: LinearLayout? = null
  private var searchView: SearchView? = null
  private var ivBack: ImageView? = null
  private var recyclerView: RecyclerView? = null
  private var inputTipsAdapter: InputTipsAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_search)
    llSearchView = findViewById(R.id.ll_search_view)
    searchView = findViewById(R.id.search_view)
    ivBack = findViewById(R.id.iv_back)
    recyclerView = findViewById(R.id.recycler_view)
    initSearchView()
    ivBack?.setOnClickListener(this)
  }

  private fun initRecyclerView(data: MutableList<Tip>) {
    val linearLayoutManager = LinearLayoutManager(this)
    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
    recyclerView?.layoutManager = linearLayoutManager
    inputTipsAdapter = InputTipsAdapter(this, data) { tip ->
      onItemClick(tip)
    }
    recyclerView?.adapter = inputTipsAdapter
  }

  private fun initSearchView() {
    llSearchView.setMargins(marginTop = BaseActivity.STATUS_BAR_HEIGHT)
    searchView?.setOnQueryTextListener(this)

    //设置SearchView默认为展开显示
    searchView?.isIconified = false
    searchView?.onActionViewExpanded()
    searchView?.setIconifiedByDefault(true)
    searchView?.isSubmitButtonEnabled = false
    val searchEditText = searchView?.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
    searchEditText?.setBackgroundColor(Color.TRANSPARENT)
  }

  /**
   * 按下确认键触发，如键盘回车或搜索键
   */
  override fun onQueryTextSubmit(query: String?): Boolean {
    val intent = Intent()
    intent.putExtra(Constants.KEY_WORDS_NAME, query)
    setResult(Constants.RESULT_CODE_KEYWORDS, intent)
    this.finish()
    return false
  }

  /**
   * 输入字符变化时触发
   */
  override fun onQueryTextChange(newText: String?): Boolean {
    if (!newText.isNullOrEmpty()) {
      val currentLocation = intent.getParcelableExtra<LatLonPoint>(Constants.EXTRA_LATLNG)
      val inputQuery = InputtipsQuery(newText, null).apply {
        location = currentLocation
        cityLimit = false
      }
      val inputTips = Inputtips(this@SearchActivity.applicationContext, inputQuery)
      inputTips.setInputtipsListener(this) //输入提示监听
      inputTips.requestInputtipsAsyn()
    } else {
      if (inputTipsAdapter != null) {
        inputTipsAdapter?.notifyDataSetChanged()
      }
    }
    return false
  }

  /**
   * 输入提示回调
   */
  override fun onGetInputtips(tipList: MutableList<Tip>?, rCode: Int) {
    if (rCode == 1000 && tipList != null) {
      val listString: MutableList<String> = ArrayList()
      for (i in tipList.indices) {
        listString.add(tipList[i].name)
      }
      initRecyclerView(tipList)
      inputTipsAdapter?.notifyDataSetChanged()
    } else {
      ToastUtil.showError(this, rCode)
    }
  }

  private fun onItemClick(tip: Tip) {
    val intent = Intent()
    intent.putExtra(Constants.EXTRA_TIP, tip)
    setResult(Constants.RESULT_CODE_INPUTTIPS, intent)
    this.finish()
  }

  override fun onClick(view: View) {
    if (view.id == R.id.iv_back) {
      this.finish()
    }
  }
}