package com.luqian.androidx.ui.wifi;

import android.Manifest;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.fubao.baselibrary.base.BaseVmActivity;
import com.luqian.androidx.R;
import com.luqian.androidx.databinding.ActivityWifiBinding;
import com.luqian.androidx.model.eventbus.WifiScanResultEvent;
import com.luqian.androidx.uitls.WifiUtil;
import com.luqian.androidx.widget.InputWifiPsdPop;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupAnimation;
import com.permissionx.guolindev.PermissionX;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * 仅仅作为 WIFI 测试用，随时可能废弃
 */
public class WifiActivity extends BaseVmActivity<WifiViewModel, ActivityWifiBinding> {

    private WifiBroadCastReceiver mReceiver;
    private WifiAdapter mAdapter;
    private InputWifiPsdPop mInputWifiPsdPop;
    private List<ScanResult> mScanList;
    private ScanResult mScanResultSelected;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_wifi;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        bind.tvRefreshWifi.setOnClickListener(v -> WifiUtil.getInstance(this).scanWifi());
        registerWifi();
        mAdapter = new WifiAdapter();
        mAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            showInputWifiPsdPop();
            this.mScanResultSelected = mScanList.get(position);
        });
        bind.rvWifi.setAdapter(mAdapter);
    }


    @Override
    protected void initData() {
        requestPer();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    private void showInputWifiPsdPop() {
        dismiss();
        if (mInputWifiPsdPop == null) {
            mInputWifiPsdPop = (InputWifiPsdPop) new XPopup.Builder(this)
                    .hasStatusBar(false)
                    .hasNavigationBar(false)
                    .dismissOnTouchOutside(true)
                    .popupAnimation(PopupAnimation.NoAnimation)
                    .asCustom(new InputWifiPsdPop(this));
        }
        mInputWifiPsdPop.show();
    }


    /**
     * 连接 WIFI
     *
     * @param psd
     */
    public void connect(String psd) {
        toast("开发中");
    }


    public void dismiss() {
        if (mInputWifiPsdPop != null && mInputWifiPsdPop.isShow()) {
            mInputWifiPsdPop.dismiss();
        }
    }


    private void registerWifi() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mReceiver = new WifiBroadCastReceiver();
        registerReceiver(mReceiver, filter);
    }


    private void requestPer() {
        PermissionX.init(this)
                .permissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .onForwardToSettings((scope, deniedList) ->
                        scope.showForwardToSettingsDialog(deniedList,
                                "请在设置中开启授权",
                                "去设置权限"))
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        WifiUtil.getInstance(this).scanWifi();
                    } else {
                        toast("请先授权");
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiEvent(WifiScanResultEvent event) {
        mScanList = WifiUtil.getInstance(this).getWifiScanList();
        mAdapter.setList(mScanList);
    }

}