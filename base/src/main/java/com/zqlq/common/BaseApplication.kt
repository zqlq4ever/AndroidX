package com.zqlq.common

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.tencent.mmkv.MMKV

open class BaseApplication : Application() {

    companion object {
        @JvmStatic
        lateinit var context: BaseApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        // 这两行必须写在 init 之前，否则这些配置在 init 过程中将无效
        if (BuildConfig.DEBUG) {
            // 打印日志
            ARouter.openLog()
            // 开启调试模式(如果在 InstantRun 模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug()
        }
        // 尽可能早，推荐在 Application 中初始化
        ARouter.init(this)
        val logLevel = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.WARN
        XLog.init(logLevel)
        MMKV.initialize(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouter.getInstance().destroy()
    }
}
