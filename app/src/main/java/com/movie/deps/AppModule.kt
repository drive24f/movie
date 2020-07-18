package com.movie.deps

import android.app.Application
import android.content.Context
import com.movie.db.FavManager
import com.movie.ui.detail.DetailPresenter
import com.movie.ui.favourites.FavPresenter
import com.movie.ui.home.HomePresenter
import com.movie.ui.home.fragment.nowplaying.NowPlayingPresenter
import com.movie.ui.home.fragment.popular.PopularPresenter
import com.movie.ui.home.fragment.toprated.TopRatedPresenter
import com.movie.ui.splash.SplashPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application.applicationContext as Context
    }

    @Provides
    @Singleton
    fun provideFavManager(context: Context): FavManager {
        return FavManager(context)
    }

    @Provides
    @Singleton
    fun provideFavPresenter(context: Context): FavPresenter {
        return FavPresenter(context)
    }

    @Provides
    @Singleton
    fun provideNowPlayingPresenter(context: Context): NowPlayingPresenter {
        return NowPlayingPresenter(context)
    }

    @Provides
    @Singleton
    fun providePopularPresenter(context: Context): PopularPresenter {
        return PopularPresenter(context)
    }

    @Provides
    @Singleton
    fun provideTopRatedPresenter(context: Context): TopRatedPresenter {
        return TopRatedPresenter(context)
    }

    @Provides
    @Singleton
    fun provideSplashPresenter(context: Context): SplashPresenter {
        return SplashPresenter(context)
    }

    @Provides
    @Singleton
    fun provideHomePresenter(context: Context): HomePresenter {
        return HomePresenter(context)
    }

    @Provides
    @Singleton
    fun provideDetailPresenter(context: Context): DetailPresenter {
        return DetailPresenter(context)
    }
}