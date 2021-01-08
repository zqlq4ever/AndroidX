package com.luqian.androidx

import android.app.Application
import android.content.Context

/**
 * 文件描述:
 * 作者:  luqian
 * 创建时间:  2021/1/8
 *
 */
class App : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}