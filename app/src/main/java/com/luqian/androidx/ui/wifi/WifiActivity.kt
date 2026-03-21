package com.luqian.androidx.ui.wifi

import android.Manifest
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import com.fubao.baselibrary.base.BaseVmActivity
import com.luqian.androidx.R
import com.luqian.androidx.databinding.ActivityWifiBinding
import com.luqian.androidx.model.eventbus.WifiScanResultEvent
import com.luqian.androidx.uitls.WifiUtil
import com.luqian.androidx.widget.InputWifiPsdPop
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.permissionx.guolindev.PermissionX
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 仅仅作为 WIFI 测试用，随时可能废弃
 */
class WifiActivity : BaseVmActivity<WifiViewModel, ActivityWifiBinding>() {

    private var mReceiver: WifiBroadCastReceiver? = null
    private var mAdapter: WifiAdapter? = null
    private var mInputWifiPsdPop: InputWifiPsdPop? = null
    private var mScanList: List<ScanResult>? = null
    private var mScanResultSelected: ScanResult? = null

    override fun getLayoutId(): Int = R.layout.activity_wifi

    override fun initView(savedInstanceState: Bundle?) {
        bind.tvRefreshWifi.setOnClickListener { WifiUtil.getInstance(this).scanWifi() }
        registerWifi()
        mAdapter = WifiAdapter().apply {
            setOnItemClickListener { _, _, position ->
                showInputWifiPsdPop()
                mScanResultSelected = mScanList?.get(position)
            }
        }
        bind.rvWifi.adapter = mAdapter
    }

    override fun initData() {
        requestPer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mReceiver?.let { unregisterReceiver(it) }
    }

    private fun showInputWifiPsdPop() {
        dismiss()
        if (mInputWifiPsdPop == null) {
            mInputWifiPsdPop = XPopup.Builder(this)
                .hasStatusBar(false)
                .hasNavigationBar(false)
                .dismissOnTouchOutside(true)
                .popupAnimation(PopupAnimation.NoAnimation)
                .asCustom(InputWifiPsdPop(this)) as InputWifiPsdPop
        }
        mInputWifiPsdPop?.show()
    }

    /**
     * 连接 WIFI
     */
    fun connect(psd: String) {
        toast("开发中")
    }

    fun dismiss() {
        if (mInputWifiPsdPop != null && mInputWifiPsdPop?.isShow == true) {
            mInputWifiPsdPop?.dismiss()
        }
    }

    private fun registerWifi() {
        val filter = IntentFilter().apply {
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        }
        mReceiver = WifiBroadCastReceiver()
        registerReceiver(mReceiver, filter)
    }

    private fun requestPer() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "请在设置中开启授权",
                    "去设置权限"
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    WifiUtil.getInstance(this).scanWifi()
                } else {
                    toast("请先授权")
                }
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWifiEvent(event: WifiScanResultEvent) {
        mScanList = WifiUtil.getInstance(this).getWifiScanList()
        mAdapter?.submitList(mScanList)
    }
}
