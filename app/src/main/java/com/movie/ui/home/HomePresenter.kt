package com.movie.ui.home

import android.content.Context
import com.movie.MainApp

class HomePresenter(context: Context) {

    private lateinit var view: HomeView

    init {
        MainApp.create(context).provides().inject(presenter = this)
    }

    fun set(view: HomeView) {
        this.view = view
    }
}