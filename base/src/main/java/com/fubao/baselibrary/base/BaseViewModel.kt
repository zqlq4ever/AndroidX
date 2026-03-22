package com.fubao.baselibrary.base;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author LUQIAN
 * @date 2021/5/10
 */
public class BaseViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable;

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public void addDisposable(Observable<?> flowable, BaseVmObserver observer) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(
                flowable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(observer));
    }


    public void removeDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        removeDisposable();
    }

}