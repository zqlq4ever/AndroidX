package com.luqian.androidx.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.luqian.androidx.R
import com.luqian.androidx.core.ActivityHelper
import com.luqian.androidx.databinding.ActivityHomeBinding
import com.luqian.androidx.ui.CornerActivity
import com.luqian.androidx.ui.ScaleViewActivity
import com.luqian.androidx.ui.wifi.WifiActivity

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
                }
            }
        }
        bind.rvMenu.adapter = menuAdapter
        menuAdapter.setList(
            arrayListOf(
                "手势旋转缩放 view",
                "view 圆角",
                "wifi"
            )
        )
    }

}