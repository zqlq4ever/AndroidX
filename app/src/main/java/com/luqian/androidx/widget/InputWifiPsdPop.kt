package com.luqian.androidx.widget

import android.annotation.SuppressLint
import android.view.View
import android.widget.EditText
import com.luqian.androidx.R
import com.luqian.androidx.ui.wifi.WifiActivity
import com.lxj.xpopup.core.CenterPopupView

/**
 * @author Administrator
 * @date 2021/6/28
 */
@SuppressLint("ViewConstructor")
class InputWifiPsdPop(private val context: WifiActivity) : CenterPopupView(context) {

    private var mEtWifiPsd: EditText? = null

    private val data = ""


    override fun getImplLayoutId(): Int {
        return R.layout.pop_input_wifi_psd
    }


    override fun onCreate() {
        super.onCreate()
        mEtWifiPsd = findViewById(R.id.et_wifi_psd)
        findViewById<View>(R.id.tv_connect).setOnClickListener { v: View? ->
            context.connect(password)
            dismiss()
        }
    }


    override fun doAfterDismiss() {
        super.doAfterDismiss()
        clear()
    }


    private fun clear() {
        if (mEtWifiPsd != null) {
            mEtWifiPsd!!.setText(data)
        }
    }

    private val password: String
        get() = mEtWifiPsd?.text.toString()
}