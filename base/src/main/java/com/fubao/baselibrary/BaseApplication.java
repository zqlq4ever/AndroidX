package com.fubao.baselibrary;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.tencent.mmkv.MMKV;


public class BaseApplication extends Application {

    private static BaseApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //  这两行必须写在 init 之前，否则这些配置在 init 过程中将无效
        if (BuildConfig.DEBUG) {
            //  打印日志
            ARouter.openLog();
            //  开启调试模式(如果在 InstantRun 模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug();
        }
        //  尽可能早，推荐在 Application 中初始化
        ARouter.init(this);
        XLog.init(LogLevel.ALL);
        MMKV.initialize(this);
    }

    public static BaseApplication getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }
}
