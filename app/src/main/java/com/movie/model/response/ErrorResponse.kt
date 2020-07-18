package com.movie.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName(value = "status_code")
    @Expose
    var statusCode: String = "",
    @SerializedName(value = "status_message")
    @Expose
    var statusMessage: String = ""
)