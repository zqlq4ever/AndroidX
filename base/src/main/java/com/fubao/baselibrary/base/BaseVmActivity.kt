package com.fubao.baselibrary.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.fubao.baselibrary.utils.ActivityManager
import com.fubao.baselibrary.utils.ScreenUtils
import com.fubao.baselibrary.widget.LoadingDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

abstract class BaseVmActivity<VM : BaseViewModel, DB : ViewDataBinding> : AppCompatActivity() {

    companion object {
        private const val TAG = "BaseVmActivity"
    }

    private var activityWeakReference: WeakReference<Activity>? = null
    private var dialog: LoadingDialog? = null

    /**
     * 当前 activity 的实例
     */
    protected var currentActivity: Activity? = null

    protected lateinit var viewmodel: VM

    protected lateinit var bind: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        setWindowParams()
        super.onCreate(savedInstanceState)
        currentActivity = this
        activityWeakReference = WeakReference(this)
        ActivityManager.add(activityWeakReference!!)
        viewmodel = ViewModelProvider(this)[getVMClass()]
        bind = DataBindingUtil.setContentView(this, getLayoutId())

        initView(savedInstanceState)
        initLiveData()
        initData()
    }

    /**
     * 获取泛型对相应的 Class 对象
     */
    @Suppress("UNCHECKED_CAST")
    fun getVMClass(): Class<VM> {
        // 返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type
        val type = this.javaClass.genericSuperclass as ParameterizedType
        // 返回表示此类型实际类型参数的 Type 对象的数组()，想要获取第一泛型的Class，索引写 0
        return type.actualTypeArguments[0] as Class<VM>
    }

    /**
     * 获取资源 ID
     */
    protected abstract fun getLayoutId(): Int

    protected abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化数据
     */
    protected abstract fun initData()

    @CallSuper
    protected open fun initLiveData() {
        viewmodel.isLoading.observe(this) { isLoading ->
            if (isLoading == true) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun setWindowParams() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val params = window.attributes
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.attributes = params

        val orientation = ScreenUtils.getScreenOrientation(this)
        // 确定了屏幕方向以后就锁定屏幕方向,禁止随意旋转。
        // 还可以用getResources().getConfiguration().orientation获取屏幕方向，
        // 但是一定要注意在Configuration和ActivityInfo中，表示同样屏幕方向的常量值却不完全一样，前者表示横向的
        // 值是Configuration.ORIENTATION_LANDSCAPE=2，后者是ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE=0,
        // 而setRequestedOrientation是以ActivityInfo中的常量为准的，所以如果直接将Configuration的屏幕常量作为
        // 参数的话，在横向的时候是会失效的，必须经过转换才行。
        requestedOrientation = orientation
    }

    protected fun toTheActivity(router: String, finishSelf: Boolean) {
        if (!TextUtils.isEmpty(router)) {
            ARouter.getInstance().build(router).navigation()
        }
        if (finishSelf) {
            finish()
        }
    }

    protected fun toTheActivity(router: String) {
        toTheActivity(router, false)
    }

    protected fun toTheActivity(router: String, key: String, bundle: Bundle) {
        if (!TextUtils.isEmpty(router)) {
            ARouter.getInstance().build(router).withBundle(key, bundle).navigation()
        }
    }

    protected fun toTheActivity(router: String, key: String, param: String) {
        if (!TextUtils.isEmpty(router)) {
            ARouter.getInstance().build(router).withString(key, param).navigation()
        }
    }

    protected fun toTheActivity(router: String, key: String, param: Int) {
        if (!TextUtils.isEmpty(router)) {
            ARouter.getInstance().build(router).withInt(key, param).navigation()
        }
    }

    fun toTheActivity(clazz: Class<out Activity>, args: Bundle?) {
        val intent = Intent(this, clazz)
        if (null != args) {
            intent.putExtras(args)
        }
        startActivity(intent)
    }

    protected fun toActivity(clazz: Class<out Activity>) {
        toActivity(clazz, null)
    }

    protected fun toActivity(clazz: Class<out Activity>, args: Bundle?) {
        val intent = Intent(this, clazz)
        if (null != args) {
            intent.putExtras(args)
        }
        startActivity(intent)
    }

    fun showLoading() {
        if (dialog == null) {
            dialog = LoadingDialog(currentActivity!!)
        }
        dialog?.setCancelable(false)
        dialog?.show()
    }

    fun hideLoading() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    protected fun toast(@StringRes resId: Int) {
        Toast.makeText(applicationContext, resId, Toast.LENGTH_SHORT).show()
    }

    protected fun toast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        currentActivity = null
        activityWeakReference?.let { ActivityManager.remove(it) }
        if (::bind.isInitialized) {
            bind.unbind()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    open fun onMessageEvent(event: EmptyEvent) {
    }
}
