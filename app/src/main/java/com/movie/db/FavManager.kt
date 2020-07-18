package com.movie.db

import android.content.Context
import com.movie.MainApp
import io.objectbox.Box
import io.objectbox.kotlin.equal
import io.objectbox.kotlin.query
import io.objectbox.query.Query

class FavManager(val context: Context) {

    private var favUser: Box<FavUser> = MainApp.create(context).getBoxStore().boxFor(FavUser::class.java)
    private lateinit var favQuery: Query<FavUser>

    fun getFavAll(): MutableList<FavUser> {
        return favUser.all.toMutableList()
    }

    fun remove(movieId: Int) {
        favUser.query()
            .equal(FavUser_.idMovie, movieId)
            .notEqual(FavUser_.id, 0)
            .build()
            .remove()
    }

    fun findMovie(movieId: Int): MutableList<FavUser> {
        favQuery = favUser.query { this.equal(FavUser_.idMovie, movieId) }
        return favQuery.find()
    }

    fun isAvailable(movieId: Int): Boolean {
        favQuery = favUser.query { this.equal(FavUser_.idMovie, movieId) }
        val findMovie: MutableList<FavUser> = favQuery.find()
        return when (findMovie.size == 1) {
            true -> true
            false -> false
        }
    }

    fun insertFavUser(idMovie: Int, title: String, posterPath: String, overview: String, releaseDate: String) {
        val user = FavUser()
        user.run {
            this.idMovie = idMovie
            this.title = title
            this.posterPath = posterPath
            this.overview = overview
            this.releaseDate = releaseDate
        }
        favUser.put(user)
    }
}