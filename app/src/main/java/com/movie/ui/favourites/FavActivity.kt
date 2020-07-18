package com.movie.ui.favourites

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.movie.BaseActivity
import com.movie.MainApp
import com.movie.R
import com.movie.databinding.ActivityFavBinding
import com.movie.db.FavManager
import com.movie.db.FavUser
import com.movie.ui.detail.DetailActivity
import com.movie.ui.favourites.adapter.FavAdapter
import javax.inject.Inject

class FavActivity: BaseActivity(), FavView {

    @Inject
    lateinit var presenter: FavPresenter

    private lateinit var binding: ActivityFavBinding
    private lateinit var boxManager: FavManager
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var favAdapter: FavAdapter = FavAdapter()

    companion object {
        fun create() = FavActivity()
    }

    fun start(context: Context) {
        val intent = Intent(context, FavActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApp.create(application).provides().inject(activity = this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fav)
        presenter.set(this)

        boxManager = FavManager(context = this)

        initButton()
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        when (boxManager.getFavAll().size) {
            0 -> binding.textNotFound.visibility = VISIBLE
            else -> binding.textNotFound.visibility = GONE
        }
        favAdapter.set(boxManager.getFavAll())
    }

    private fun initButton() {
        binding.let {
            it.btnBack.setOnClickListener { onBackPressed() }

            favAdapter.getItem { item, _ ->
                if (item is FavUser) DetailActivity.create().start(
                    context = this,
                    movieId = item.idMovie.toString()
                )
            }
        }
    }

    private fun initAdapter() {
        linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            adapter = favAdapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        fadeIn()
    }
}