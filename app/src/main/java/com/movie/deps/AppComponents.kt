package com.movie.deps

import com.movie.ui.detail.DetailActivity
import com.movie.ui.detail.DetailPresenter
import com.movie.ui.favourites.FavActivity
import com.movie.ui.favourites.FavPresenter
import com.movie.ui.home.HomeActivity
import com.movie.ui.home.HomePresenter
import com.movie.ui.home.fragment.nowplaying.NowPlayingFragment
import com.movie.ui.home.fragment.nowplaying.NowPlayingPresenter
import com.movie.ui.home.fragment.popular.PopularFragment
import com.movie.ui.home.fragment.popular.PopularPresenter
import com.movie.ui.home.fragment.toprated.TopRatedFragment
import com.movie.ui.home.fragment.toprated.TopRatedPresenter
import com.movie.ui.splash.SplashActivity
import com.movie.ui.splash.SplashPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ApiModule::class])
interface AppComponents {

    fun inject(activity: SplashActivity)
    fun inject(activity: HomeActivity)
    fun inject(activity: DetailActivity)
    fun inject(activity: FavActivity)

    fun inject(fragment: NowPlayingFragment)
    fun inject(fragment: PopularFragment)
    fun inject(fragment: TopRatedFragment)

    fun inject(presenter: FavPresenter)
    fun inject(presenter: SplashPresenter)
    fun inject(presenter: HomePresenter)
    fun inject(presenter: DetailPresenter)
    fun inject(presenter: NowPlayingPresenter)
    fun inject(presenter: PopularPresenter)
    fun inject(presenter: TopRatedPresenter)
}