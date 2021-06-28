package com.luqian.androidx.ui.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.elvishew.xlog.XLog;
import com.luqian.androidx.model.eventbus.WifiScanResultEvent;

import org.greenrobot.eventbus.EventBus;

public class WifiBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiBroadCastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            //  wifi 开关变化通知
            case WifiManager.WIFI_STATE_CHANGED_ACTION:

                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        XLog.d(TAG, "WIFI_STATE_DISABLED");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        XLog.d(TAG, "WIFI_STATE_ENABLED");
                        break;
                }

                break;

            //  wifi 扫描结果通知
            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                XLog.d(TAG, "SCAN_RESULTS_AVAILABLE_ACTION");
                EventBus.getDefault().post(new WifiScanResultEvent());
                break;
        }
    }
}
