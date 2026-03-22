package com.fubao.baselibrary.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * @author LUQIAN
 * @date 2021/5/10
 */
open class BaseViewModel : ViewModel() {

    private var compositeDisposable: CompositeDisposable? = null

    val isLoading = MutableLiveData<Boolean>()

    fun <T> addDisposable(flowable: Observable<T>, observer: BaseVmObserver<T>) {
        if (compositeDisposable == null) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(
            flowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        )
    }

    fun removeDisposable() {
        compositeDisposable?.dispose()
    }

    override fun onCleared() {
        super.onCleared()
        removeDisposable()
    }
}
