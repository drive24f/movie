package com.movie.ui.detail

import android.content.Context
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.movie.MainApp
import com.movie.R
import com.movie.api.ApiService
import com.movie.common.Constanst
import com.movie.common.Constanst.APIKEY
import com.movie.common.Constanst.DELAY
import com.movie.common.Constanst.LANGUAGE
import com.movie.common.Constanst.PAGEONE
import com.movie.common.Constanst.TYPE_CONTENT
import com.movie.model.response.MovieReviewResponse
import com.movie.model.response.NowPlayingResponse
import com.movie.model.response.VideoDetailResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DetailPresenter(val context: Context) {

    @Inject
    lateinit var api: ApiService

    private lateinit var view: DetailView
    private var disposeApi: Disposable = Disposables.empty()

    init {
        MainApp.create(context).provides().inject(presenter = this)
    }

    fun set(view: DetailView) {
        this.view = view
    }

    fun dispose() {
        when{
            !disposeApi.isDisposed -> disposeApi.dispose()
        }
    }

    fun fetchDetail(movieId: String) {
        disposeApi = api.fetchDetailMovie(movieId, APIKEY, LANGUAGE)
            .subscribeOn(Schedulers.io())
            .delay(DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view.showLoading() }
            .doAfterTerminate { view.hideLoading() }
            .map { view.onRetrieveDetail(it) }
            .flatMap { observableVideo(movieId) }
            .map { view.onRetrieveVideo(it) }
            .flatMap { observableReview(movieId) }
            .map { view.onRetrieveReview(it) }
            .subscribe({}, { catchError(it)})
    }

    fun fetchLoadMore(page: String) {

    }

    fun loadingList(): MutableList<MovieReviewResponse.Result> {
        val dummyList: MutableList<MovieReviewResponse.Result> = mutableListOf()
        repeat(times = 3) {
            dummyList.add(MovieReviewResponse.Result(type = TYPE_CONTENT, isLoading = true))
        }
        return dummyList
    }

    fun loadMore(): MutableList<MovieReviewResponse.Result> {
        val loadMoreList: MutableList<MovieReviewResponse.Result> = mutableListOf()
        repeat(times = 1) {
            loadMoreList.add(MovieReviewResponse.Result(type = TYPE_CONTENT, isLoading = true))
        }
        return loadMoreList
    }

    private fun observableVideo(movieId: String): Observable<VideoDetailResponse> {
        return api.fetchVideo(movieId, APIKEY, LANGUAGE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun observableReview(movieId: String): Observable<MovieReviewResponse> {
        return api.fetchReview(movieId, APIKEY, LANGUAGE, PAGEONE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun catchError(e: Throwable) {
        when (e) {
            is UnknownHostException -> view.onError(context.getString(R.string.error_message))
            is SocketTimeoutException -> view.onError(context.getString(R.string.error_message))
            is IOException -> view.onError(context.getString(R.string.error_message))
            is HttpException -> view.onError("${context.getString(R.string.error_message)}\nerror: ${e.code()}")
        }
    }
}