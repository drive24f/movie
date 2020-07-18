package com.movie.ui.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.movie.MainApp
import com.movie.R
import com.movie.common.*
import com.movie.common.Constanst.DEVELOPER_KEY
import com.movie.common.Constanst.MAX_PAGE
import com.movie.common.Constanst.MIN_PAGE
import com.movie.common.Constanst.MOVIE_ID_KEY
import com.movie.common.Constanst.STATIC_VIDEO
import com.movie.common.Constanst.ZERO
import com.movie.databinding.ActivityDetailBinding
import com.movie.db.FavManager
import com.movie.model.response.MovieDetailResponse
import com.movie.model.response.MovieReviewResponse
import com.movie.model.response.VideoDetailResponse
import com.movie.ui.detail.adapter.ReviewAdapter
import javax.inject.Inject

class DetailActivity: YouTubeBaseActivity(), DetailView, YouTubePlayer.OnInitializedListener {

    @Inject
    lateinit var presenter: DetailPresenter

    @Inject
    lateinit var favManager: FavManager

    private lateinit var binding: ActivityDetailBinding
    private lateinit var movieModel: MovieDetailResponse
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var reviewAdapter: ReviewAdapter = ReviewAdapter()

    companion object {
        fun create() = DetailActivity()

        private var movieKey: String = ""
        private var currentPage: Int = 1
        private var totalPage: Int = 0
    }

    fun start(context: Context, movieId: String) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(MOVIE_ID_KEY, movieId)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApp.create(application).provides().inject(activity = this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        presenter.set(this)

        initButton()
        initScroll()
        initAdapter()
        checkBookmark()

        presenter.fetchDetail(movieId = getMovieId())
        binding.swiperefresh.setOnRefreshListener { presenter.fetchDetail(movieId = getMovieId()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

    private fun getMovieId(): String {
        return intent.getStringExtra(MOVIE_ID_KEY)?: ""
    }

    private fun initButton() {
        binding.let {
            it.btnBack.setOnClickListener { onBackPressed() }

            it.btnSave.setOnClickListener {
                when (favManager.isAvailable(getMovieId().toInt())) {
                    true -> {
                        favManager.remove(getMovieId().toInt())
                        binding.imageBookmark.setGray(R.drawable.ic_wishlist)
                    }
                    false -> {
                        favManager.insertFavUser(
                            idMovie = movieModel.id,
                            title = movieModel.title,
                            posterPath = movieModel.posterPath,
                            overview =  movieModel.overview,
                            releaseDate = movieModel.releaseDate
                        )
                        binding.imageBookmark.setRed(R.drawable.ic_wishlist)
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            adapter = reviewAdapter
        }
    }

    private fun initScroll() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount: Int = recyclerView.layoutManager!!.itemCount
                if (totalItemCount == linearLayoutManager.findLastVisibleItemPosition() + MIN_PAGE) {
                    currentPage++
                    binding.recyclerView.removeOnScrollListener(scrollListener)
                    if (currentPage != totalPage) {
                        reviewAdapter.setLoading(presenter.loadMore())
                        presenter.fetchLoadMore(page = currentPage.toString())
                    }
                }
            }
        }
    }

    private fun checkBookmark() {
        binding.let {
            it.btnSave.isEnabled = false
            when (favManager.isAvailable(getMovieId().toInt())) {
                true -> it.imageBookmark.setRed(R.drawable.ic_wishlist)
                false -> it.imageBookmark.setGray(R.drawable.ic_wishlist)
            }
        }
    }

    override fun showLoading() {
        binding.swiperefresh.isRefreshing = true
    }

    override fun hideLoading() {
        binding.swiperefresh.isRefreshing = false
    }

    override fun onError(message: String) {
        binding.let {
            it.layoutError.visibility = VISIBLE
            it.textError.text = message
            it.swiperefresh.isRefreshing = false
        }
    }

    override fun onRetrieveDetail(model: MovieDetailResponse) {
        binding.let {
            it.textTitle.text = model.title
            it.textDate.text = model.releaseDate.formatDate()
            it.textDesc.text = model.overview
            it.imageMovie.setImage(Constanst.IMAGE_URL + model.posterPath)
            it.btnSave.isEnabled = true
        }

        movieModel = model
    }

    override fun onRetrieveVideo(model: VideoDetailResponse) {
        binding.let {
            if (model.results?.size != 0) {
                movieKey = model.results!![0].key
                it.layoutVideo.visibility = VISIBLE
            } else {
                movieKey = STATIC_VIDEO
                it.layoutVideo.visibility = GONE
            }
            it.youtubeView.initialize(DEVELOPER_KEY, this)
        }
    }

    override fun onRetrieveReview(model: MovieReviewResponse) {
        binding.let {
            totalPage = model.totalPages
            if (totalPage > MIN_PAGE && model.results?.size == MAX_PAGE) {
                it.recyclerView.addOnScrollListener(scrollListener)
            }

            when {
                model.results?.size != ZERO -> {
                    it.layoutEmpty.visibility = GONE
                }
                else -> {
                    it.layoutEmpty.visibility = VISIBLE
                }
            }

            reviewAdapter.set(model.results ?: mutableListOf())
        }
    }

    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
        p1?.cueVideo(movieKey)
    }

    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}