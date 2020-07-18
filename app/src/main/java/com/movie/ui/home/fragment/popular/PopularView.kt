package com.movie.ui.home.fragment.popular

import com.movie.model.response.PopularResponse

interface PopularView {
    fun showLoading()
    fun hideLoading()
    fun onError(message: String)
    fun onRetrieveData(model: PopularResponse)
    fun onRetrieveMoreData(model: PopularResponse)
    fun removeListener()
}