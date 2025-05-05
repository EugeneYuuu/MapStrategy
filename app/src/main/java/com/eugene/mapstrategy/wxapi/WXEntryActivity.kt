package com.eugene.mapstrategy.wxapi

import android.app.Activity
import android.os.Bundle
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode
import com.tencent.mm.opensdk.modelmsg.SendAuth.Resp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class WXEntryActivity : Activity(), IWXAPIEventHandler {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val api = WXAPIFactory.createWXAPI(this, "你的AppID")
    api.handleIntent(intent, this)
  }

  override fun onReq(req: BaseReq) {}

  override fun onResp(resp: BaseResp) {
    if (resp is Resp) {
      when (resp.errCode) {
        ErrCode.ERR_OK -> {
          var code = resp.code // 获取临时授权码
        }
        ErrCode.ERR_USER_CANCEL -> {}
        ErrCode.ERR_AUTH_DENIED -> {}
      }
    }
    finish()
  }
}