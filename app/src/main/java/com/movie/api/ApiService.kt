package com.movie.api

import com.movie.model.response.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(value = Url.NOW_PLAYING)
    fun fetchNowPlaying(
        @Query(value = "api_key") apiKey: String,
        @Query(value = "language") language: String,
        @Query(value = "page") page: String,
        @Query(value = "region") region: String
    ): Observable<NowPlayingResponse>

    @GET(value = Url.POPULAR)
    fun fetchPopular(
        @Query(value = "api_key") apiKey: String,
        @Query(value = "language") language: String,
        @Query(value = "page") page: String,
        @Query(value = "region") region: String
    ): Observable<PopularResponse>

    @GET(value = Url.TOP_RATED)
    fun fetchTopRated(
        @Query(value = "api_key") apiKey: String,
        @Query(value = "language") language: String,
        @Query(value = "page") page: String,
        @Query(value = "region") region: String
    ): Observable<TopRatedResponse>

    @GET(value = Url.DETAIL_MOVIE)
    fun fetchDetailMovie(
        @Path(value = "movie_id") movieId: String,
        @Query(value = "api_key") apiKey: String,
        @Query(value = "language") language: String
    ): Observable<MovieDetailResponse>

    @GET(value = Url.DETAIL_VIDEO)
    fun fetchVideo(
        @Path(value = "movie_id") movieId: String,
        @Query(value = "api_key") apiKey: String,
        @Query(value = "language") language: String
    ): Observable<VideoDetailResponse>

    @GET(value = Url.MOVIE_REVIEW)
    fun fetchReview(
        @Path(value = "movie_id") reviewId: String,
        @Query(value = "api_key") apiKey: String,
        @Query(value = "language") language: String,
        @Query(value = "page") page: String
    ): Observable<MovieReviewResponse>
}