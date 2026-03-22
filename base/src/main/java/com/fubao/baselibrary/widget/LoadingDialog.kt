package com.fubao.baselibrary.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.fubao.baselibrary.R;


/**
 * 自定义弹窗
 */
public class LoadingDialog extends Dialog {

    TextView tvLoadingTx;
    ImageView ivLoading;

    public LoadingDialog(Context context) {
        this(context, R.style.loading_dialog, "玩命加载中...");
    }

    public LoadingDialog(Context context, String string) {
        this(context, R.style.loading_dialog, string);
    }

    protected LoadingDialog(Context context, int theme, String string) {
        super(context, theme);
        //  点击其他区域时 true 关闭弹窗 false 不关闭弹窗
        setCanceledOnTouchOutside(true);
        //  加载布局
        setContentView(R.layout.loading_dialog);
        tvLoadingTx = findViewById(R.id.tv_loading_tx);
        tvLoadingTx.setText(string);
        ivLoading = findViewById(R.id.iv_loading);
        // 加载动画
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        // 使用 ImageView 显示动画
        ivLoading.startAnimation(animation);
        //  居中显示
        getWindow().getAttributes().gravity = Gravity.CENTER;
        //  背景透明度 取值范围 0 ~ 1
        getWindow().getAttributes().dimAmount = 0.5f;
    }


    @Override
    public void dismiss() {
        //  关闭动画
        ivLoading.clearAnimation();
        super.dismiss();
    }
}
