package com.luqian.androidx.ui.wifi

import android.net.wifi.ScanResult
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.elvishew.xlog.XLog
import com.luqian.androidx.R

class WifiAdapter : BaseQuickAdapter<ScanResult, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: android.content.Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(LayoutInflater.from(context).inflate(R.layout.item_wifi, parent, false))
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: ScanResult?) {
        item?.let {
            if (it.SSID != null) {
                holder.itemView.findViewById<TextView>(R.id.tv_wifi_name).text = it.SSID
            }
            val capabilities = it.capabilities
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
}
