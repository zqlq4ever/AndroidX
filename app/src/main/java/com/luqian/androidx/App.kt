package com.luqian.androidx

import android.app.Application
import android.content.Context
import com.luqian.androidx.core.ActivityHelper

/**
 * 文件描述:
 * 作者:  luqian
 * 创建时间:  2021/1/8
 *
 */
class App : Application() {

    companion object {
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        ActivityHelper.init(this)
    }
}