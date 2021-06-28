package com.luqian.androidx.widget;

import android.annotation.SuppressLint;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.luqian.androidx.R;
import com.luqian.androidx.ui.wifi.WifiActivity;
import com.lxj.xpopup.core.CenterPopupView;

/**
 * @author Administrator
 * @date 2021/6/28
 */
@SuppressLint("ViewConstructor")
public class InputWifiPsdPop extends CenterPopupView {

    private EditText mEtWifiPsd;
    private WifiActivity context;
    private final String data = "";


    public InputWifiPsdPop(@NonNull WifiActivity context) {
        super(context);
        this.context = context;
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_input_wifi_psd;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        mEtWifiPsd = findViewById(R.id.et_wifi_psd);
        findViewById(R.id.tv_connect).setOnClickListener(v -> {
            context.connect(getPassword());
            dismiss();
        });
    }


    @Override
    protected void doAfterDismiss() {
        super.doAfterDismiss();
        clear();
    }


    public void clear() {
        if (mEtWifiPsd != null) {
            mEtWifiPsd.setText(data);
        }
    }


    public String getPassword() {
        return mEtWifiPsd.getText().toString();
    }
}
