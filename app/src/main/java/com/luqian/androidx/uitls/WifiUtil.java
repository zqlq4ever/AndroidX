package com.luqian.androidx.uitls;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WiFi连接管理
 * 申请权限
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
 * 动态权限
 * Manifest.permission.ACCESS_COARSE_LOCATION
 * Manifest.permission.ACCESS_FINE_LOCATION
 */
public class WifiUtil {

    private static WifiUtil sWifiUtil = null;

    private static final String TAG = "WifiUtil";

    private Context mContext;

    private final WifiManager mWifiManager;


    private WifiUtil(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mContext = context;
    }


    public static WifiUtil getInstance(Context context) {
        if (sWifiUtil == null) {
            synchronized (WifiUtil.class) {
                if (sWifiUtil == null) {
                    sWifiUtil = new WifiUtil(context);
                }
            }
        }
        return sWifiUtil;
    }


    /**
     * wifi是否打开
     *
     * @return
     */
    public boolean isWifiEnable() {
        boolean isEnable = false;
        if (mWifiManager != null) {
            if (mWifiManager.isWifiEnabled()) {
                isEnable = true;
            }
        }
        return isEnable;
    }

    /**
     * 打开WiFi
     */
    public void openWifi() {
        if (mWifiManager != null && !isWifiEnable()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WiFi
     */
    public void closeWifi() {
        if (mWifiManager != null && isWifiEnable()) {
            mWifiManager.setWifiEnabled(false);
        }
    }


    /**
     * 扫描 wifi
     */
    public void scanWifi() {
        if (!isWifiEnable()) {
            openWifi();
        }
        mWifiManager.startScan();
    }


    /**
     * 有密码连接
     *
     * @param ssid
     * @param pws
     */
    public void connectWifiPwd(String ssid, String pws) {
        mWifiManager.disableNetwork(mWifiManager.getConnectionInfo().getNetworkId());
        WifiConfiguration wifiConfig = getWifiConfig(ssid, pws, true);
        int netId = mWifiManager.addNetwork(wifiConfig);
        mWifiManager.enableNetwork(netId, true);
    }

    /**
     * 无密码连接
     *
     * @param ssid
     */
    public void connectWifiNoPwd(String ssid) {
        mWifiManager.disableNetwork(mWifiManager.getConnectionInfo().getNetworkId());
        int netId = mWifiManager.addNetwork(getWifiConfig(ssid, "", false));
        mWifiManager.enableNetwork(netId, true);
    }


    /**
     * 获取 wifi 扫描结果
     */
    public List<ScanResult> getWifiScanList() {
        if (mWifiManager == null) {
            return null;
        }

        List<ScanResult> results = mWifiManager.getScanResults();
        if (results == null) {
            return null;
        }

        List<ScanResult> newList = new ArrayList<>();
        WifiInfo info = mWifiManager.getConnectionInfo();
        for (int i = 0; i < results.size(); i++) {
            ScanResult scanResult = results.get(i);
            //  当前已连接设备不显示在列表中
            if (info != null && info.getBSSID().equals(scanResult.BSSID)) {
                continue;
            }

            if (TextUtils.isEmpty(scanResult.SSID)) {
                continue;
            }

            //  该热点 SSID 是否已在列表中
            int position = getItemPosition(newList, scanResult);
            // 已在列表
            if (position != -1) {
                //  相同 SSID 热点，取信号强的
                if (newList.get(position).level < scanResult.level) {
                    newList.remove(position);
                    newList.add(position, scanResult);
                }
            } else {
                newList.add(scanResult);
            }
        }
        //  按信号强度排序
        Collections.sort(newList, (o1, o2) -> o2.level - o1.level);

        return newList;
    }


    /**
     * 返回 item 在 list 中的坐标
     */
    private int getItemPosition(List<ScanResult> list, ScanResult item) {
        for (int i = 0; i < list.size(); i++) {
            if (item.SSID.equals(list.get(i).SSID)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * wifi设置
     *
     * @param ssid
     * @param pws
     * @param isHasPws
     */
    private WifiConfiguration getWifiConfig(String ssid, String pws, boolean isHasPws) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExist(ssid);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        if (isHasPws) {
            config.preSharedKey = "\"" + pws + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }

    /**
     * 得到配置好的网络连接
     *
     * @param ssid
     * @return
     */
    private WifiConfiguration isExist(String ssid) {
        if (checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(mContext, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            XLog.d(TAG, "未授权： ACCESS_FINE_LOCATION 或 ACCESS_WIFI_STATE");
            return null;
        }
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }


    private static final int SECURITY_NONE = 0;
    private static final int SECURITY_WEP = 1;
    private static final int SECURITY_PSK = 2;
    private static final int SECURITY_EAP = 3;

    /**
     * WIFI 加密类型
     */
    private int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)
                || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }
}
