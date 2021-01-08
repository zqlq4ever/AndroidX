package com.example.gesturescaleandscroll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gesturescaleandscroll.databinding.ActivityMainBinding
import com.example.gesturescaleandscroll.gesture.GestureScaleHelper
import com.example.gesturescaleandscroll.uitls.WaterMarkUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //  添加水印
        WaterMarkUtil.sInstance.show(this, "我是水印")
        //  缩放 view
        GestureScaleHelper.bind(this, root, child).run {
            isFullGroup = true
        }
    }
}