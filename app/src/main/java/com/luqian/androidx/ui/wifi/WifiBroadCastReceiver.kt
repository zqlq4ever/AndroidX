package com.luqian.androidx.ui.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.elvishew.xlog.XLog
import com.luqian.androidx.model.eventbus.WifiScanResultEvent
import org.greenrobot.eventbus.EventBus

class WifiBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        when (intent.action) {
            WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)) {
                    WifiManager.WIFI_STATE_DISABLED -> XLog.d(TAG, "WIFI_STATE_DISABLED")
                    WifiManager.WIFI_STATE_ENABLED -> XLog.d(TAG, "WIFI_STATE_ENABLED")
                }
            }
            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                XLog.d(TAG, "SCAN_RESULTS_AVAILABLE_ACTION")
                EventBus.getDefault().post(WifiScanResultEvent())
            }
        }
    }

    companion object {
        private const val TAG = "WifiBroadCastReceiver"
    }

}