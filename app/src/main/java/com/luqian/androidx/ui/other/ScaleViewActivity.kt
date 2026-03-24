package com.luqian.androidx.ui.other

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import coil3.load
import com.blankj.utilcode.util.TimeUtils
import com.luqian.androidx.R
import com.luqian.androidx.databinding.ActivityScaleBinding
import com.luqian.androidx.gesture.GestureScaleHelper
import com.luqian.androidx.uitls.WaterMarkUtil

class ScaleViewActivity : AppCompatActivity() {

    companion object {
        private val url = "https://haowallpaper.com/link/common/file/previewFileImg/17873560258071936"
    }

    private lateinit var binding: ActivityScaleBinding
    private var gestureScaleHelper: GestureScaleHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scale)

        WaterMarkUtil.sInstance.show(binding.root, TimeUtils.date2String(TimeUtils.getNowDate()))
        binding.child.load(url)

        gestureScaleHelper = GestureScaleHelper.bind(this, binding.root, binding.child).apply {
            isFullGroup = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放资源，防止内存泄漏
        gestureScaleHelper?.release()
        gestureScaleHelper = null
    }

}
