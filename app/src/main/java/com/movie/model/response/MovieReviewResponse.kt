package com.movie.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovieReviewResponse(
    @SerializedName(value = "id")
    @Expose
    var id: Int = 0,
    @SerializedName(value = "page")
    @Expose
    var page: Int = 0,
    @SerializedName(value = "results")
    @Expose
    var results: MutableList<Result>? = mutableListOf(),
    @SerializedName(value = "total_pages")
    @Expose
    var totalPages: Int = 0,
    @SerializedName(value = "total_results")
    @Expose
    var totalResults: Int = 0
) {
    class Result(
        @SerializedName(value = "type")
        @Expose
        var type: Int = 0,
        @SerializedName(value = "isLoading")
        @Expose
        var isLoading: Boolean = false,
        @SerializedName(value = "id")
        @Expose
        var id: String = "",
        @SerializedName(value = "author")
        @Expose
        var author: String = "",
        @SerializedName(value = "content")
        @Expose
        var content: String = "",
        @SerializedName(value = "url")
        @Expose
        var url: String = ""
    )
}