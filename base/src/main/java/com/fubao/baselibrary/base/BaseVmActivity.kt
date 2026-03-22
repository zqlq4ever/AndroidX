package com.fubao.baselibrary.base;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fubao.baselibrary.utils.ActivityManager;
import com.fubao.baselibrary.utils.ScreenUtils;
import com.fubao.baselibrary.widget.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;

public abstract class BaseVmActivity<VM extends BaseViewModel, DB extends ViewDataBinding> extends AppCompatActivity {

    private static final String TAG = "BaseVmActivity";

    private WeakReference<Activity> activityWeakReference;

    private LoadingDialog dialog;

    /**
     * 当前 activity 的实例
     */
    protected Activity currentActivity;

    protected VM viewmodel;

    protected DB bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setWindowParams();
        super.onCreate(savedInstanceState);
        currentActivity = this;
        activityWeakReference = new WeakReference<>(this);
        ActivityManager.add(activityWeakReference);
        viewmodel = new ViewModelProvider(this).get(getVMClass());
        bind = DataBindingUtil.setContentView(this, getLayoutId());

        initView(savedInstanceState);
        initLiveData();
        initData();
    }


    /**
     * 获取泛型对相应的 Class 对象
     */
    public Class<VM> getVMClass() {
        //  返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        //  返回表示此类型实际类型参数的 Type 对象的数组()，想要获取第一泛型的Class，索引写 0
        return (Class) type.getActualTypeArguments()[0];//<VM>
    }


    /**
     * 获取资源 ID
     */
    protected abstract int getLayoutId();


    protected abstract void initView(Bundle savedInstanceState);


    /**
     * 初始化数据
     */
    protected abstract void initData();


    @CallSuper
    protected void initLiveData() {
        if (viewmodel == null) return;
        viewmodel.isLoading.observe(this, isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    private void setWindowParams() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.setAttributes(params);

        int orientation = ScreenUtils.getScreenOrientation(this);
        // 确定了屏幕方向以后就锁定屏幕方向,禁止随意旋转。
        // 还可以用getResources().getConfiguration().orientation获取屏幕方向，
        // 但是一定要注意在Configuration和ActivityInfo中，表示同样屏幕方向的常量值却不完全一样，前者表示横向的
        // 值是Configuration.ORIENTATION_LANDSCAPE=2，后者是ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE=0,
        // 而setRequestedOrientation是以ActivityInfo中的常量为准的，所以如果直接将Configuration的屏幕常量作为
        // 参数的话，在横向的时候是会失效的，必须经过转换才行。
        setRequestedOrientation(orientation);
    }


    protected void toTheActivity(String router, boolean finishSelf) {
        if (!TextUtils.isEmpty(router)) {
            ARouter.getInstance().build(router).navigation();
        }
        if (finishSelf) {
            finish();
        }
    }


    protected void toTheActivity(String router) {
        toTheActivity(router, false);
    }


    protected void toTheActivity(String router, String key, Bundle bundle) {
        if (!TextUtils.isEmpty(router)) {
            ARouter.getInstance().build(router).withBundle(key, bundle).navigation();
        }
    }


    protected void toTheActivity(String router, String key, String param) {
        if (!TextUtils.isEmpty(router)) {
            ARouter.getInstance().build(router).withString(key, param).navigation();
        }
    }


    protected void toTheActivity(String router, String key, int param) {
        if (!TextUtils.isEmpty(router)) {
            ARouter.getInstance().build(router).withInt(key, param).navigation();
        }
    }


    public void toTheActivity(Class<? extends Activity> clazz, Bundle args) {
        Intent intent = new Intent(this, clazz);
        if (null != args) {
            intent.putExtras(args);
        }
        startActivity(intent);
    }


    protected void toActivity(Class<? extends Activity> clazz) {
        toActivity(clazz, null);
    }


    protected void toActivity(Class<? extends Activity> clazz, Bundle args) {
        Intent intent = new Intent(this, clazz);
        if (null != args) {
            intent.putExtras(args);
        }
        startActivity(intent);
    }


    public void showLoading() {
        if (dialog == null) {
            dialog = new LoadingDialog(currentActivity);
        }
        dialog.setCancelable(false);
        dialog.show();
    }


    public void hideLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    protected void toast(@StringRes int resId) {
        Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
    }


    protected void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentActivity = null;
        ActivityManager.remove(activityWeakReference);
        if (bind != null) {
            bind.unbind();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onMessageEvent(EmptyEvent event) {

    }

}
