package com.fubao.baselibrary.base

import io.reactivex.observers.DisposableObserver
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.ParseException
import java.util.concurrent.TimeoutException
import org.json.JSONException
import com.google.gson.JsonParseException

abstract class BaseVmObserver<T> : DisposableObserver<T> {

    /**
     * 是否需要显示加载框
     */
    private var isShowDialog = false
    private val vm: BaseViewModel?

    constructor(vm: BaseViewModel?) {
        this.vm = vm
    }

    constructor(vm: BaseViewModel?, isShowDialog: Boolean) : this(vm) {
        this.isShowDialog = isShowDialog
    }

    override fun onStart() {
        if (vm != null && isShowDialog) {
            vm.isLoading.value = true
        }
    }

    override fun onNext(response: T & Any) {
        onSuccess(response)
    }

    override fun onError(e: Throwable) {
        if (vm != null && isShowDialog) {
            vm.isLoading.value = false
        }
        val be: BaseException

        when (e) {
            is BaseException -> {
                onError(e.errorMsg)
                return
            }
            is HttpException -> {
                // HTTP错误
                be = BaseException(BaseException.BAD_NETWORK_MSG, e, BaseException.BAD_NETWORK)
            }
            is ConnectException, is UnknownHostException -> {
                // 连接错误
                be = BaseException(BaseException.CONNECT_ERROR_MSG, e, BaseException.CONNECT_ERROR)
            }
            is TimeoutException, is java.io.InterruptedIOException -> {
                // 连接超时
                be = BaseException(BaseException.CONNECT_TIMEOUT_MSG, e, BaseException.CONNECT_TIMEOUT)
            }
            is JsonParseException, is JSONException, is ParseException -> {
                // 解析错误
                be = BaseException(BaseException.PARSE_ERROR_MSG, e, BaseException.PARSE_ERROR)
            }
            else -> {
                be = BaseException(BaseException.OTHER_MSG, e, BaseException.OTHER)
            }
        }

        onError(be.errorMsg)
    }

    override fun onComplete() {
        // 通过 viewmodel 加载框的显示或者隐藏
        // viewmodel 只是控制器,不直接操作 UI
        if (vm != null && isShowDialog) {
            vm.isLoading.value = false
        }
    }

    abstract fun onSuccess(data: T & Any)

    open fun onError(msg: String?) {
        // ToastUtil.showToast(msg)
    }
}
