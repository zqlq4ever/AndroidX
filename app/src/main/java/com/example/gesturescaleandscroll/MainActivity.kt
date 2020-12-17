package com.example.gesturescaleandscroll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gesturescaleandscroll.databinding.ActivityMainBinding
import com.example.gesturescaleandscroll.gesture.GestureScaleHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GestureScaleHelper.bind(this, root, child)
    }
}