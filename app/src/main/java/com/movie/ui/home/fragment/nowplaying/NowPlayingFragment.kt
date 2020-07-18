package com.movie.ui.home.fragment.nowplaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.movie.BaseFragment
import com.movie.MainApp
import com.movie.common.Constanst.INIT_PAGE
import com.movie.common.Constanst.MAX_PAGE
import com.movie.common.Constanst.MIN_PAGE
import com.movie.common.Constanst.RETRY
import com.movie.common.Constanst.ZERO
import com.movie.databinding.FragmentNowPlayingBinding
import com.movie.model.response.NowPlayingResponse
import com.movie.ui.detail.DetailActivity
import com.movie.ui.home.fragment.nowplaying.adapter.NowPlayingAdapter
import java.lang.Boolean.FALSE
import javax.inject.Inject

class NowPlayingFragment : BaseFragment(), NowPlayingView {

    @Inject
    lateinit var presenter: NowPlayingPresenter

    private lateinit var getActivity: FragmentActivity
    private lateinit var binding: FragmentNowPlayingBinding
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var nowPlayingAdapter: NowPlayingAdapter = NowPlayingAdapter()

    companion object {
        private var currentPage: Int = 1
        private var totalPage: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApp.create(activity).provides().inject(fragment = this)
        activity?.let { getActivity = it }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNowPlayingBinding.inflate(inflater, container, FALSE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.set(this)

        initButton()
        initScroll()
        initAdapter()

        loadData()

        binding.swiperefresh.setOnRefreshListener {
            presenter.fetchMovie(page = INIT_PAGE)
        }
    }

    override fun onResume() {
        super.onResume()
        nowPlayingAdapter.refresh()
    }

    override fun onDetach() {
        super.onDetach()
        presenter.dispose()
        if (!nowPlayingAdapter.initCheck()) {
            nowPlayingAdapter.removeLast(presenter.retry())
        } else {
            nowPlayingAdapter.clear()
        }
    }

    private fun loadData() {
        binding.let {
            when {
                !nowPlayingAdapter.isAvailable() -> {
                    currentPage = MIN_PAGE
                    presenter.fetchMovie(page = INIT_PAGE)
                }
                currentPage == MIN_PAGE && nowPlayingAdapter.isAvailable() && totalPage == ZERO -> {
                    currentPage = MIN_PAGE
                    presenter.fetchMovie(page = INIT_PAGE)
                }
                nowPlayingAdapter.isRetry() -> {
                    it.recyclerView.removeOnScrollListener(scrollListener)
                }
                else -> {
                    if (totalPage > MIN_PAGE) {
                        it.recyclerView.addOnScrollListener(scrollListener)
                    }
                }
            }
        }
    }

    private fun initButton() {
        nowPlayingAdapter.getItem { item, _ ->
            if (item is NowPlayingResponse.Result) {
                DetailActivity.create().start(getActivity, movieId = item.id.toString())
            } else if (item == RETRY) {
                if (currentPage != totalPage) {
                    nowPlayingAdapter.update(presenter.loadMore())
                    presenter.fetchLoadMore(page = currentPage.toString())
                } else {
                    presenter.fetchMovie(page = INIT_PAGE)
                }
            }
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
                        nowPlayingAdapter.setLoading(presenter.loadMore())
                        presenter.fetchLoadMore(page = currentPage.toString())
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            adapter = nowPlayingAdapter
        }
    }

    override fun showLoading() {
        if (!binding.swiperefresh.isRefreshing) nowPlayingAdapter.set(presenter.loadingList())
    }

    override fun hideLoading() {
        binding.swiperefresh.isRefreshing = false
    }

    override fun onError(message: String) {
        binding.recyclerView.removeOnScrollListener(scrollListener)
        nowPlayingAdapter.update(presenter.retry())
    }

    override fun removeListener() {
        binding.recyclerView.removeOnScrollListener(scrollListener)
    }

    override fun onRetrieveData(model: NowPlayingResponse) {
        binding.let {
            totalPage = model.totalPages
            if (totalPage > MIN_PAGE && model.results?.size == MAX_PAGE) {
                it.recyclerView.addOnScrollListener(scrollListener)
            }
            it.swiperefresh.isRefreshing = false
            nowPlayingAdapter.set(model.results ?: mutableListOf())
        }
    }

    override fun onRetrieveMoreData(model: NowPlayingResponse) {
        binding.let {
            it.recyclerView.removeOnScrollListener(scrollListener)
            it.recyclerView.addOnScrollListener(scrollListener)
            nowPlayingAdapter.update(model.results ?: mutableListOf())
        }
    }
}