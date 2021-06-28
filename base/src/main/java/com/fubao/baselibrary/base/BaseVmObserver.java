package com.fubao.baselibrary.base;

import androidx.annotation.NonNull;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;


public abstract class BaseVmObserver<T> extends DisposableObserver<T> {

    /**
     * 是否需要显示加载框
     */
    private boolean isShowDialog;
    private final BaseViewModel vm;


    public BaseVmObserver(BaseViewModel vm) {
        this.vm = vm;
    }

    public BaseVmObserver(BaseViewModel vm, boolean isShowDialog) {
        this.vm = vm;
        this.isShowDialog = isShowDialog;
    }


    @Override
    protected void onStart() {
        if (vm != null && isShowDialog) {
            vm.isLoading.setValue(true);
        }
    }


    @Override
    public void onNext(@NonNull T response) {
        onSuccess(response);
    }


    @Override
    public void onError(@NonNull Throwable e) {
        if (vm != null && isShowDialog) {
            vm.isLoading.setValue(false);
        }
        BaseException be;

        if (e instanceof BaseException) {
            be = (BaseException) e;
            onError(be.getErrorMsg());
        } else if (e instanceof HttpException) {
            //   HTTP错误
            be = new BaseException(BaseException.BAD_NETWORK_MSG, e, BaseException.BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {
            //   连接错误
            be = new BaseException(BaseException.CONNECT_ERROR_MSG, e, BaseException.CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {
            //  连接超时
            be = new BaseException(BaseException.CONNECT_TIMEOUT_MSG, e, BaseException.CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            //  解析错误
            be = new BaseException(BaseException.PARSE_ERROR_MSG, e, BaseException.PARSE_ERROR);
        } else {
            be = new BaseException(BaseException.OTHER_MSG, e, BaseException.OTHER);
        }

        onError(be.getErrorMsg());
    }


    @Override
    public void onComplete() {
        //  通过 viewmodel 加载框的显示或者隐藏
        //  viewmodel 只是控制器,不直接操作 UI
        if (vm != null && isShowDialog) {
            vm.isLoading.setValue(false);
        }
    }


    public abstract void onSuccess(T data);


    public void onError(String msg) {
//        ToastUtil.showToast(msg);
    }

}
