package com.movie.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PopularResponse(
    @SerializedName(value = "page")
    @Expose
    var page: Int = 0,
    @SerializedName(value = "total_results")
    @Expose
    var totalResults: Int = 0,
    @SerializedName(value = "total_pages")
    @Expose
    var totalPages: Int = 0,
    @SerializedName(value = "results")
    @Expose
    var results: MutableList<Result>? = mutableListOf()
) {
    data class Result(
        @SerializedName(value = "type")
        @Expose
        var type: Int = 0,
        @SerializedName(value = "isLoading")
        @Expose
        var isLoading: Boolean = false,
        @SerializedName(value = "id")
        @Expose
        var id: Int = 0,
        @SerializedName(value = "title")
        @Expose
        var title: String = "",
        @SerializedName(value = "poster_path")
        @Expose
        var posterPath: String = "",
        @SerializedName(value = "backdrop_path")
        @Expose
        var backdropPath: String = "",
        @SerializedName(value = "overview")
        @Expose
        var overview: String = "",
        @SerializedName(value = "release_date")
        @Expose
        var releaseDate: String = ""
    )
}