package com.movie.ui.home.fragment.toprated

import com.movie.model.response.TopRatedResponse

interface TopRatedView {
    fun showLoading()
    fun hideLoading()
    fun onError(message: String)
    fun onRetrieveData(model: TopRatedResponse)
    fun onRetrieveMoreData(model: TopRatedResponse)
    fun removeListener()
}