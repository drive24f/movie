package com.movie.db

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class FavUser {
    @Id
    var id: Long = 0
    var idMovie: Int = 0
    var title: String = ""
    var posterPath: String = ""
    var overview: String = ""
    var releaseDate: String = ""
}