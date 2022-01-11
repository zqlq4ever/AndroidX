package com.luqian.androidx.ui.home

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ToastUtils
import com.luqian.androidx.R
import com.luqian.androidx.core.ActivityHelper
import com.luqian.androidx.databinding.ActivityHomeBinding
import com.luqian.androidx.ui.camerax.CameraActivity
import com.luqian.androidx.ui.ecg.EcgActivity
import com.luqian.androidx.ui.other.CornerActivity
import com.luqian.androidx.ui.other.ScaleViewActivity
import com.luqian.androidx.ui.wifi.WifiActivity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ForwardScope

class HomeActivity : AppCompatActivity() {

    private lateinit var bind: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val menuAdapter = MenuAdapter().apply {
            setOnItemClickListener { _, _, position ->
                when (position) {
                    0 -> ActivityHelper.startActivity(ScaleViewActivity::class.java)
                    1 -> ActivityHelper.startActivity(CornerActivity::class.java)
                    2 -> ActivityHelper.startActivity(WifiActivity::class.java)
                    3 -> {
                        PermissionX.init(this@HomeActivity)
                            .permissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                                scope.showForwardToSettingsDialog(
                                    deniedList,
                                    "请在设置中开启授权",
                                    "去设置权限"
                                )
                            }
                            .request { allGranted: Boolean, _, _ ->
                                if (allGranted) {
                                    ActivityHelper.startActivity(CameraActivity::class.java)
                                } else {
                                    ToastUtils.showShort("请先授权")
                                }
                            }
                    }
                    4 -> ActivityHelper.startActivity(EcgActivity::class.java)
                }
            }
        }
        bind.rvMenu.adapter = menuAdapter
        menuAdapter.setList(
            arrayListOf(
                "view 手势旋转缩放 ",
                "view 圆角",
                "wifi",
                "CameraX",
                "心电图",
            )
        )
    }

}