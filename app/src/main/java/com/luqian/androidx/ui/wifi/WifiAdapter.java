package com.luqian.androidx.ui.wifi;

import android.net.wifi.ScanResult;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.elvishew.xlog.XLog;
import com.luqian.androidx.R;

/**
 * @author Administrator
 * @date 2021/6/25
 */
public class WifiAdapter extends BaseQuickAdapter<ScanResult, BaseViewHolder> {

    public WifiAdapter() {
        super(R.layout.item_wifi);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ScanResult result) {
        if (result.SSID != null) {
            baseViewHolder.setText(R.id.tv_wifi_name, result.SSID);
        }
        String capabilities = result.capabilities;
        if (!TextUtils.isEmpty(capabilities)) {
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                XLog.d("WifiAdapter", "wpa");
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                XLog.d("WifiAdapter", "wep");
            } else {
                XLog.d("WifiAdapter", "no");
            }
        }
    }
}
