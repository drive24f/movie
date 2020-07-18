package com.movie.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VideoDetailResponse(
    @SerializedName(value = "id")
    @Expose
    var id: Int = 0,
    @SerializedName(value = "results")
    @Expose
    var results: MutableList<Result>? = mutableListOf()
) {
    class Result(
        @SerializedName(value = "id")
        @Expose
        var id: String = "",
        @SerializedName(value = "key")
        @Expose
        var key: String = ""
    )
}