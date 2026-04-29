package com.luqian.androidx.ui.wifi

import android.Manifest
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.zqlq.common.base.BaseVmActivity
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
import com.zqlq.common.R as BaseR

/**
 * WiFi 扫描页面 - 采用深色主题设计，参考 CameraActivity 风格
 */
class WifiActivity : BaseVmActivity<WifiViewModel, ActivityWifiBinding>() {

    override val enableEventBus = true

    private var mReceiver: WifiBroadCastReceiver? = null
    private var isReceiverRegistered = false
    private var mAdapter: WifiAdapter? = null
    private var mInputWifiPsdPop: InputWifiPsdPop? = null
    private var mScanList: List<ScanResult>? = null
    private var mScanResultSelected: ScanResult? = null
    private var mRefreshRotateAnim: RotateAnimation? = null

    override fun getLayoutId(): Int = R.layout.activity_wifi

    override fun initView(savedInstanceState: Bundle?) {
        setupRefreshAnimation()
        setupClickListeners()
        registerWifi()
        setupAdapter()
    }

    private fun setupRefreshAnimation() {
        mRefreshRotateAnim = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 1000
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
        }
    }

    private fun setupClickListeners() {
        bind.ivRefresh.setOnClickListener {
            startRefreshAnimation()
            WifiUtil.getInstance(this).scanWifi()
        }
    }

    private fun startRefreshAnimation() {
        bind.ivRefresh.startAnimation(mRefreshRotateAnim)
    }

    private fun stopRefreshAnimation() {
        bind.ivRefresh.clearAnimation()
    }

    private fun setupAdapter() {
        mAdapter = WifiAdapter().apply {
            setOnItemClickListener { _, _, position ->
                mScanList?.get(position)?.let { scanResult ->
                    mScanResultSelected = scanResult
                    showInputWifiPsdPop()
                }
            }
        }
        bind.rvWifi.adapter = mAdapter
    }

    override fun initData() {
        requestPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismiss()
        mInputWifiPsdPop = null
        if (isReceiverRegistered) {
            mReceiver?.let { unregisterReceiver(it) }
            isReceiverRegistered = false
        }
        stopRefreshAnimation()
    }

    private fun showInputWifiPsdPop() {
        dismiss()
        if (mInputWifiPsdPop == null) {
            mInputWifiPsdPop = XPopup.Builder(this)
                .hasStatusBar(false)
                .hasNavigationBar(false)
                .dismissOnTouchOutside(true)
                .popupAnimation(PopupAnimation.NoAnimation)
                .asCustom(InputWifiPsdPop(this) { connect(it) }) as InputWifiPsdPop
        }
        mInputWifiPsdPop?.show()
    }

    /**
     * 连接 WiFi
     */
    fun connect(psd: String) {
        toast(getString(BaseR.string.tip_developing))
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
        isReceiverRegistered = true
    }

    private fun requestPermission() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    getString(BaseR.string.permission_location_wifi),
                    getString(BaseR.string.btn_setting)
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    startRefreshAnimation()
                    WifiUtil.getInstance(this).scanWifi()
                } else {
                    toast(getString(BaseR.string.permission_camera_photo))
                    updateEmptyState(true)
                }
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWifiEvent(event: WifiScanResultEvent) {
        stopRefreshAnimation()
        mScanList = WifiUtil.getInstance(this).getWifiScanList()

        val isEmpty = mScanList.isNullOrEmpty()
        updateEmptyState(isEmpty)

        if (!isEmpty) {
            mAdapter?.submitList(mScanList)
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        bind.llEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        bind.rvWifi.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}
