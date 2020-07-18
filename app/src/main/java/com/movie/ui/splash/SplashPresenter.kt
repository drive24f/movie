package com.movie.ui.splash

import android.content.Context
import com.movie.MainApp
import com.movie.common.Constanst.COUNTER
import com.movie.common.Constanst.ZERO
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit

class SplashPresenter(context: Context) {

    private lateinit var view: SplashView
    private var disposeTimer: Disposable = Disposables.empty()

    init {
        MainApp.create(context).provides().inject(presenter = this)
    }

    fun set(view: SplashView) {
        this.view = view
    }

    fun dispose() {
        when {
            !disposeTimer.isDisposed -> disposeTimer.dispose()
        }
    }

    fun startTimer() {
        disposeTimer = Observable.range(ZERO, COUNTER)
            .flatMap { v -> Observable.just(v).delay((COUNTER - v).toLong(), TimeUnit.SECONDS) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate { view.onClose() }
            .subscribe { }
    }
}