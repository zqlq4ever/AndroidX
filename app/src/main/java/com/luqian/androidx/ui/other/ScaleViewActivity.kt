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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scale)

        WaterMarkUtil.sInstance.show(binding.root, TimeUtils.date2String(TimeUtils.getNowDate()))
        binding.child.load(url)

        GestureScaleHelper.bind(this, binding.root, binding.child).run {
            isFullGroup = true
        }
    }

}
