package com.luqian.androidx.ui.wifi

import android.net.wifi.ScanResult
import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.elvishew.xlog.XLog
import com.luqian.androidx.R

/**
 * @author Administrator
 * @date 2021/6/25
 */
class WifiAdapter : BaseQuickAdapter<ScanResult, BaseViewHolder>(R.layout.item_wifi) {

    override fun convert(holder: BaseViewHolder, item: ScanResult) {
        if (item.SSID != null) {
            holder.setText(R.id.tv_wifi_name, item.SSID)
        }
        val capabilities = item.capabilities
        if (!TextUtils.isEmpty(capabilities)) {
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                XLog.d("WifiAdapter", "wpa")
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                XLog.d("WifiAdapter", "wep")
            } else {
                XLog.d("WifiAdapter", "no")
            }
        }
    }
}