package com.movie

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.movie.db.MyObjectBox
import com.movie.deps.ApiModule
import com.movie.deps.AppComponents
import com.movie.deps.AppModule
import com.movie.deps.DaggerAppComponents
import io.objectbox.BoxStore

@Suppress("DEPRECATION")
class MainApp : Application() {

    private var mainApp: MainApp = this@MainApp
    private lateinit var appComponents: AppComponents
    private lateinit var boxStore: BoxStore

    companion object {
        @Synchronized
        fun create(context: Context?): MainApp {
            return context?.applicationContext as MainApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(mainApp)
        setAppComponents()
        setObjectBox()
    }

    fun provides(): AppComponents {
        return appComponents
    }

    fun getBoxStore(): BoxStore {
        return boxStore
    }

    private fun setObjectBox() {
        boxStore = MyObjectBox.builder()
            .androidContext(mainApp)
            .build()
    }

    private fun setAppComponents() {
        appComponents = DaggerAppComponents.builder()
            .appModule(AppModule(mainApp))
            .apiModule(ApiModule())
            .build()
    }
}