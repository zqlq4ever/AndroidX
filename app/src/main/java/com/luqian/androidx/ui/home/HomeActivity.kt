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
import com.zqlq.common.R as BaseR

class HomeActivity : AppCompatActivity() {

    private lateinit var bind: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val menuAdapter = MenuAdapter()
        menuAdapter.setOnItemClickListener { _, _, position ->
            when (position) {
                0 -> ActivityHelper.startActivity(ScaleViewActivity::class.java)
                1 -> ActivityHelper.startActivity(CornerActivity::class.java)
                2 -> ActivityHelper.startActivity(WifiActivity::class.java)
                3 -> {
                    PermissionX.init(this@HomeActivity)
                        .permissions(Manifest.permission.CAMERA)
                        .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                            scope.showForwardToSettingsDialog(
                                deniedList,
                                getString(BaseR.string.permission_camera),
                                getString(BaseR.string.btn_setting)
                            )
                        }
                        .request { allGranted: Boolean, _, _ ->
                            if (allGranted) {
                                ActivityHelper.startActivity(CameraActivity::class.java)
                            } else {
                                ToastUtils.showShort(getString(BaseR.string.permission_camera_photo))
                            }
                        }
                }

                4 -> ActivityHelper.startActivity(EcgActivity::class.java)
            }
        }
        bind.rvMenu.adapter = menuAdapter
        menuAdapter.submitList(
            arrayListOf(
                MenuItem(getString(BaseR.string.menu_gesture), R.drawable.ic_menu_gesture),
                MenuItem(getString(BaseR.string.menu_corner), R.drawable.ic_menu_corner),
                MenuItem(getString(BaseR.string.menu_wifi), R.drawable.ic_menu_wifi),
                MenuItem(getString(BaseR.string.menu_camera), R.drawable.ic_menu_camera),
                MenuItem(getString(BaseR.string.menu_ecg), R.drawable.ic_menu_ecg)
            )
        )
    }

}
