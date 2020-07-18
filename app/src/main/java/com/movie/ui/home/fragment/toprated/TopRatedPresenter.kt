package com.movie.ui.home.fragment.toprated

import android.content.Context
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.movie.MainApp
import com.movie.R
import com.movie.api.ApiService
import com.movie.common.Constanst
import com.movie.common.Constanst.APIKEY
import com.movie.common.Constanst.DELAY
import com.movie.common.Constanst.LANGUAGE
import com.movie.common.Constanst.REGION_ID
import com.movie.common.Constanst.TYPE_CONTENT
import com.movie.common.Constanst.TYPE_RETRY
import com.movie.model.response.TopRatedResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TopRatedPresenter(val context: Context) {

    @Inject
    lateinit var api: ApiService

    private lateinit var view: TopRatedView
    private var disposeTopRated: Disposable = Disposables.empty()
    private var disposeLoadMore: Disposable = Disposables.empty()

    init {
        MainApp.create(context).provides().inject(presenter = this)
    }

    fun set(view: TopRatedView) {
        this.view = view
    }

    fun dispose() {
        when {
            !disposeTopRated.isDisposed -> disposeTopRated.dispose()
            !disposeLoadMore.isDisposed -> disposeLoadMore.dispose()
        }
    }

    fun fetchMovie(page: String) {
        disposeTopRated = api.fetchTopRated(APIKEY, LANGUAGE, page, REGION_ID)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { view.showLoading()}
            .doAfterTerminate { }
            .delay(DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ view.onRetrieveData(it) }) { catchError(it) }
    }

    fun fetchLoadMore(page: String) {
        disposeLoadMore = api.fetchTopRated(APIKEY, LANGUAGE, page, REGION_ID)
            .subscribeOn(Schedulers.io())
            .delay(DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view.removeListener() }
            .doAfterTerminate { }
            .subscribe({ view.onRetrieveMoreData(it) }) { catchError(it) }
    }

    fun loadingList(): MutableList<TopRatedResponse.Result> {
        val dummyList: MutableList<TopRatedResponse.Result> = mutableListOf()
        repeat(times = 3) {
            dummyList.add(TopRatedResponse.Result(type = TYPE_CONTENT, isLoading = true))
        }
        return dummyList
    }

    fun loadMore(): MutableList<TopRatedResponse.Result> {
        val loadMoreList: MutableList<TopRatedResponse.Result> = mutableListOf()
        repeat(times = 1) {
            loadMoreList.add(TopRatedResponse.Result(type = TYPE_CONTENT, isLoading = true))
        }
        return loadMoreList
    }

    fun retry(): MutableList<TopRatedResponse.Result> {
        val loadMoreList: MutableList<TopRatedResponse.Result> = mutableListOf()
        repeat(times = 1) {
            loadMoreList.add(TopRatedResponse.Result(type = TYPE_RETRY, isLoading = true))
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