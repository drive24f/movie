package com.movie.ui.favourites

import android.content.Context
import com.movie.MainApp

class FavPresenter(val context: Context) {

    private lateinit var view: FavView

    init {
        MainApp.create(context).provides().inject(presenter = this)
    }

    fun set(view: FavView) {
        this.view = view
    }
}