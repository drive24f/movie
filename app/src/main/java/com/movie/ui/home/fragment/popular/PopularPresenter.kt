package com.movie.ui.home.fragment.popular

import android.content.Context
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.movie.MainApp
import com.movie.R
import com.movie.api.ApiService
import com.movie.common.Constanst.APIKEY
import com.movie.common.Constanst.DELAY
import com.movie.common.Constanst.LANGUAGE
import com.movie.common.Constanst.REGION_ID
import com.movie.common.Constanst.TYPE_CONTENT
import com.movie.common.Constanst.TYPE_RETRY
import com.movie.model.response.PopularResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PopularPresenter(val context: Context) {

    @Inject
    lateinit var api: ApiService

    private lateinit var view: PopularView
    private var disposePopular: Disposable = Disposables.empty()
    private var disposeLoadMore: Disposable = Disposables.empty()

    init {
        MainApp.create(context).provides().inject(presenter = this)
    }

    fun set(view: PopularView) {
        this.view = view
    }

    fun dispose() {
        when {
            !disposePopular.isDisposed -> disposePopular.dispose()
            !disposeLoadMore.isDisposed -> disposeLoadMore.dispose()
        }
    }

    fun fetchMovie(page: String) {
        disposePopular = api.fetchPopular(APIKEY, LANGUAGE, page, REGION_ID)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { view.showLoading()}
            .doAfterTerminate { }
            .delay(DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ view.onRetrieveData(it) }) { catchError(it) }
    }

    fun fetchLoadMore(page: String) {
        disposeLoadMore = api.fetchPopular(APIKEY, LANGUAGE, page, REGION_ID)
            .subscribeOn(Schedulers.io())
            .delay(DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view.removeListener() }
            .doAfterTerminate { }
            .subscribe({ view.onRetrieveMoreData(it) }) { catchError(it) }
    }

    fun loadingList(): MutableList<PopularResponse.Result> {
        val dummyList: MutableList<PopularResponse.Result> = mutableListOf()
        repeat(times = 3) {
            dummyList.add(PopularResponse.Result(type = TYPE_CONTENT, isLoading = true))
        }
        return dummyList
    }

    fun loadMore(): MutableList<PopularResponse.Result> {
        val loadMoreList: MutableList<PopularResponse.Result> = mutableListOf()
        repeat(times = 1) {
            loadMoreList.add(PopularResponse.Result(type = TYPE_CONTENT, isLoading = true))
        }
        return loadMoreList
    }

    fun retry(): MutableList<PopularResponse.Result> {
        val loadMoreList: MutableList<PopularResponse.Result> = mutableListOf()
        repeat(times = 1) {
            loadMoreList.add(PopularResponse.Result(type = TYPE_RETRY, isLoading = true))
        }
        return loadMoreList
    }

    private fun catchError(e: Throwable) {
        when (e) {
            is UnknownHostException -> view.onError(context.getString(R.string.error_message))
            is retrofit2.HttpException -> view.onError(context.getString(R.string.error_message))
            is SocketTimeoutException -> view.onError(context.getString(R.string.error_message))
            is IOException -> view.onError(context.getString(R.string.error_message))
            is HttpException -> view.onError(context.getString(R.string.error_message))
        }
    }
}