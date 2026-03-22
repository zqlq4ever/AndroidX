package com.luqian.androidx.uitls

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.elvishew.xlog.XLog
import java.util.Collections

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
class WifiUtil private constructor(context: Context) {

    companion object {
        @Volatile
        private var sWifiUtil: WifiUtil? = null
        private const val TAG = "WifiUtil"

        @JvmStatic
        fun getInstance(context: Context): WifiUtil {
            return sWifiUtil ?: synchronized(this) {
                sWifiUtil ?: WifiUtil(context).also { sWifiUtil = it }
            }
        }

        private const val SECURITY_NONE = 0
        private const val SECURITY_WEP = 1
        private const val SECURITY_PSK = 2
        private const val SECURITY_EAP = 3
    }

    private val mContext: Context = context.applicationContext
    private val mWifiManager: WifiManager? = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    /**
     * wifi是否打开
     */
    fun isWifiEnable(): Boolean {
        return mWifiManager?.isWifiEnabled ?: false
    }

    /**
     * 打开WiFi
     */
    fun openWifi() {
        if (mWifiManager != null && !isWifiEnable()) {
            mWifiManager.isWifiEnabled = true
        }
    }

    /**
     * 关闭WiFi
     */
    fun closeWifi() {
        if (mWifiManager != null && isWifiEnable()) {
            mWifiManager.isWifiEnabled = false
        }
    }

    /**
     * 扫描 wifi
     */
    fun scanWifi() {
        if (!isWifiEnable()) {
            openWifi()
        }
        mWifiManager?.startScan()
    }

    /**
     * 有密码连接
     *
     * @param ssid
     * @param pws
     */
    fun connectWifiPwd(ssid: String, pws: String) {
        mWifiManager?.connectionInfo?.networkId?.let { mWifiManager.disableNetwork(it) }
        val wifiConfig = getWifiConfig(ssid, pws, true)
        val netId = mWifiManager?.addNetwork(wifiConfig) ?: -1
        mWifiManager?.enableNetwork(netId, true)
    }

    /**
     * 无密码连接
     *
     * @param ssid
     */
    fun connectWifiNoPwd(ssid: String) {
        mWifiManager?.connectionInfo?.networkId?.let { mWifiManager.disableNetwork(it) }
        val netId = mWifiManager?.addNetwork(getWifiConfig(ssid, "", false)) ?: -1
        mWifiManager?.enableNetwork(netId, true)
    }

    /**
     * 获取 wifi 扫描结果
     */
    fun getWifiScanList(): List<ScanResult>? {
        if (mWifiManager == null) {
            return null
        }

        val results = mWifiManager.scanResults ?: return null

        val newList = ArrayList<ScanResult>()
        val info: WifiInfo? = mWifiManager.connectionInfo
        for (scanResult in results) {
            // 当前已连接设备不显示在列表中
            if (info != null && info.bssid == scanResult.BSSID) {
                continue
            }

            if (TextUtils.isEmpty(scanResult.SSID)) {
                continue
            }

            // 该热点 SSID 是否已在列表中
            val position = getItemPosition(newList, scanResult)
            // 已在列表
            if (position != -1) {
                // 相同 SSID 热点，取信号强的
                if (newList[position].level < scanResult.level) {
                    newList.removeAt(position)
                    newList.add(position, scanResult)
                }
            } else {
                newList.add(scanResult)
            }
        }
        // 按信号强度排序
        Collections.sort(newList) { o1, o2 -> o2.level - o1.level }

        return newList
    }

    /**
     * 返回 item 在 list 中的坐标
     */
    private fun getItemPosition(list: List<ScanResult>, item: ScanResult): Int {
        for (i in list.indices) {
            if (item.SSID == list[i].SSID) {
                return i
            }
        }
        return -1
    }

    /**
     * wifi设置
     *
     * @param ssid
     * @param pws
     * @param isHasPws
     */
    private fun getWifiConfig(ssid: String, pws: String, isHasPws: Boolean): WifiConfiguration {
        val config = WifiConfiguration()
        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()
        config.SSID = "\"$ssid\""

        val tempConfig = isExist(ssid)
        if (tempConfig != null) {
            mWifiManager?.removeNetwork(tempConfig.networkId)
        }
        if (isHasPws) {
            config.preSharedKey = "\"$pws\""
            config.hiddenSSID = true
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            config.status = WifiConfiguration.Status.ENABLED
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        }
        return config
    }

    /**
     * 得到配置好的网络连接
     *
     * @param ssid
     * @return
     */
    private fun isExist(ssid: String): WifiConfiguration? {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
        ) {
            XLog.d(TAG, "未授权： ACCESS_FINE_LOCATION 或 ACCESS_WIFI_STATE")
            return null
        }
        val configs = mWifiManager?.configuredNetworks ?: return null
        for (config in configs) {
            if (config.SSID == "\"$ssid\"") {
                return config
            }
        }
        return null
    }

    /**
     * WIFI 加密类型
     */
    private fun getSecurity(config: WifiConfiguration): Int {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)
            || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)
        ) {
            return SECURITY_EAP
        }
        return if (config.wepKeys[0] != null) SECURITY_WEP else SECURITY_NONE
    }
}
