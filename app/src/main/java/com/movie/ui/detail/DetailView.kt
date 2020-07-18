package com.movie.ui.detail

import com.movie.model.response.MovieDetailResponse
import com.movie.model.response.MovieReviewResponse
import com.movie.model.response.VideoDetailResponse

interface DetailView {
    fun showLoading()
    fun hideLoading()
    fun onError(message: String)
    fun onRetrieveDetail(model: MovieDetailResponse)
    fun onRetrieveVideo(model: VideoDetailResponse)
    fun onRetrieveReview(model: MovieReviewResponse)
}