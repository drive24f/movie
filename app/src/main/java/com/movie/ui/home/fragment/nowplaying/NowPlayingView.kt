package com.movie.ui.home.fragment.nowplaying

import com.movie.model.response.NowPlayingResponse

interface NowPlayingView {
    fun showLoading()
    fun hideLoading()
    fun onError(message: String)
    fun removeListener()
    fun onRetrieveData(model: NowPlayingResponse)
    fun onRetrieveMoreData(model: NowPlayingResponse)
}