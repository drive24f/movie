package com.movie.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(
    @SerializedName(value = "id")
    @Expose
    var id: Int = 0,
    @SerializedName(value = "genres")
    @Expose
    var genres: MutableList<Genres>? = mutableListOf(),
    @SerializedName(value = "title")
    @Expose
    var title: String = "",
    @SerializedName(value = "original_title")
    @Expose
    var originalTitle: String = "",
    @SerializedName(value = "overview")
    @Expose
    var overview: String = "",
    @SerializedName(value = "popularity")
    @Expose
    var popularity: Double = 0.0,
    @SerializedName(value = "poster_path")
    @Expose
    var posterPath: String = "",
    @SerializedName(value = "production_countries")
    @Expose
    var productionCountries: MutableList<ProductionCountry>,
    @SerializedName(value = "release_date")
    @Expose
    var releaseDate: String = "",
    @SerializedName(value = "status")
    @Expose
    var status: String = "",
    @SerializedName(value = "vote_average")
    @Expose
    var voteAverage: Double = 0.0
) {
    class Genres(
        @SerializedName(value = "id")
        @Expose
        var id: Int = 0,
        @SerializedName(value = "name")
        @Expose
        var name: String = ""
    )

    class ProductionCountry(
        @SerializedName(value = "iso_3166_1")
        @Expose
        var iso: String = "",
        @SerializedName(value = "name")
        @Expose
        var name: String = ""
    )
}