package com.zqlq.common.widget

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.zqlq.common.R

/**
 * 自定义弹窗
 */
class LoadingDialog : Dialog {

    private var tvLoadingTx: TextView
    private var ivLoading: ImageView

    constructor(context: Context) : this(context, R.style.loading_dialog, "玩命加载中...")

    constructor(context: Context, string: String) : this(context, R.style.loading_dialog, string)

    private constructor(context: Context, theme: Int, string: String) : super(context, theme) {
        // 点击其他区域时 true 关闭弹窗 false 不关闭弹窗
        setCanceledOnTouchOutside(true)
        // 加载布局
        setContentView(R.layout.loading_dialog)
        tvLoadingTx = findViewById(R.id.tv_loading_tx)
        tvLoadingTx.text = string
        ivLoading = findViewById(R.id.iv_loading)
        // 加载动画
        val animation = AnimationUtils.loadAnimation(context, R.anim.loading_animation)
        // 使用 ImageView 显示动画
        ivLoading.startAnimation(animation)
        // 居中显示
        window?.attributes?.gravity = Gravity.CENTER
        // 背景透明度 取值范围 0 ~ 1
        window?.attributes?.dimAmount = 0.5f
    }

    override fun dismiss() {
        // 关闭动画
        ivLoading.clearAnimation()
        super.dismiss()
    }
}
