package com.movie.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.movie.BaseActivity
import com.movie.MainApp
import com.movie.R
import com.movie.common.Constanst.NOWPLAYING
import com.movie.common.Constanst.POPULAR
import com.movie.common.Constanst.TOPRATED
import com.movie.common.setGray
import com.movie.databinding.ActivityHomeBinding
import com.movie.ui.categories.CategoriesFragment
import com.movie.ui.favourites.FavActivity
import com.movie.ui.home.fragment.nowplaying.NowPlayingFragment
import com.movie.ui.home.fragment.popular.PopularFragment
import com.movie.ui.home.fragment.toprated.TopRatedFragment
import kotlinx.android.synthetic.main.include_btn_whishlist.view.*
import javax.inject.Inject

class HomeActivity: BaseActivity(), HomeView {

    @Inject
    lateinit var presenter: HomePresenter

    private lateinit var binding: ActivityHomeBinding

    private var nowPlayingFragment: NowPlayingFragment = NowPlayingFragment()
    private var popularFragment: PopularFragment = PopularFragment()
    private var topRatedFragment: TopRatedFragment = TopRatedFragment()
    private var categories: CategoriesFragment = CategoriesFragment()

    companion object {
        fun create() = HomeActivity()
    }

    fun start(context: Context) {
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApp.create(application).provides().inject(activity = this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        presenter.set(this)

        initPage()
        initButton()
    }

    override fun onResume() {
        super.onResume()
        binding.btnFav.imageWishList.setGray(R.drawable.ic_wishlist)
    }

    private fun initPage() {
        tabFragment(nowPlayingFragment)
    }

    private fun initButton() {
        binding.let {
            it.btnBack.setOnClickListener {
                onBackPressed()
            }

            it.btnFav.setOnClickListener {
                FavActivity.create().start(context = this)
            }

            it.btnCategories.setOnClickListener {
                categories.show(context = this)
            }

            categories.getItem { type ->
                when (type) {
                    NOWPLAYING -> tabFragment(nowPlayingFragment)
                    POPULAR -> tabFragment(popularFragment)
                    TOPRATED -> tabFragment(topRatedFragment)
                }
            }
        }
    }

    private fun tabFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.frame.id, fragment, fragment::javaClass.name)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        fadeIn()
    }
}