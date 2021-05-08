package com.luqian.androidx.ui

import android.graphics.Outline
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import coil.load
import com.luqian.androidx.R
import com.luqian.androidx.core.load
import com.luqian.androidx.databinding.ActivityCornerBinding
import com.luqian.androidx.ext.dpToPx
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX

class CornerActivity : AppCompatActivity() {

    private lateinit var bind: ActivityCornerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UltimateBarX.with(this)
            .transparent()
            .applyStatusBar()
        bind = DataBindingUtil.setContentView(this, R.layout.activity_corner)

        bind.ivTest.load("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1977046168,368269341&fm=26&gp=0.jpg")
        setViewCorner(bind.ivTest, 20.dpToPx())
    }


    /**
     * 给 view 设置圆角
     * @param radius 圆角大小 单位是 px
     */
    private fun setViewCorner(view: View, radius: Float) {

        val provider: ViewOutlineProvider = object : ViewOutlineProvider() {

            override fun getOutline(view: View, outline: Outline) {
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                val leftMargin = 0
                val topMargin = 0
                val selfRect = Rect(
                    leftMargin, topMargin,
                    rect.right - rect.left - leftMargin,
                    rect.bottom - rect.top - topMargin
                )
                outline.setRoundRect(selfRect, radius)
            }
        }
        view.outlineProvider = provider
        view.clipToOutline = true
    }

}