package com.luqian.androidx.ui.wifi

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.luqian.androidx.R
import com.zqlq.common.R as BaseR

/**
 * WiFi 列表适配器 - 深色主题风格
 */
class WifiAdapter : BaseQuickAdapter<ScanResult, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_wifi, parent, false)
        )
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: ScanResult?) {
        item?.let { scanResult ->
            val context = holder.itemView.context

            // WiFi 名称
            val wifiName = if (scanResult.SSID.isNullOrEmpty()) {
                context.getString(BaseR.string.wifi_hidden_network)
            } else {
                scanResult.SSID
            }
            holder.itemView.findViewById<TextView>(R.id.tv_wifi_name).text = wifiName

            // 计算信号强度等级 (0-4)
            val signalLevel = WifiManager.calculateSignalLevel(scanResult.level, 5)

            // 设置信号描述文字
            val signalDesc = getSignalDescription(context, signalLevel)

            // 判断是否加密
            val capabilities = scanResult.capabilities
            val securityType = when {
                capabilities.contains("WPA3") -> "WPA3"
                capabilities.contains("WPA2") -> "WPA2"
                capabilities.contains("WPA") -> "WPA"
                capabilities.contains("WEP") -> "WEP"
                else -> context.getString(BaseR.string.wifi_security_open)
            }

            val separator = context.getString(BaseR.string.wifi_separator)
            holder.itemView.findViewById<TextView>(R.id.tv_signal_strength).text =
                "$signalDesc $separator $securityType"

            // 设置信号强度图标
            val signalIconRes = when (signalLevel) {
                4 -> BaseR.drawable.ic_signal_full
                3 -> BaseR.drawable.ic_signal_good
                else -> BaseR.drawable.ic_signal_weak
            }
            holder.itemView.findViewById<ImageView>(R.id.iv_signal).setImageResource(signalIconRes)
        }
    }

    private fun getSignalDescription(context: Context, level: Int): String {
        return when (level) {
            4 -> context.getString(BaseR.string.wifi_signal_excellent)
            3 -> context.getString(BaseR.string.wifi_signal_good)
            2 -> context.getString(BaseR.string.wifi_signal_fair)
            1 -> context.getString(BaseR.string.wifi_signal_weak)
            else -> context.getString(BaseR.string.wifi_signal_poor)
        }
    }
}
