package com.movie.ui.home.fragment.toprated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.movie.BaseFragment
import com.movie.MainApp
import com.movie.common.Constanst
import com.movie.common.Constanst.INIT_PAGE
import com.movie.common.Constanst.MAX_PAGE
import com.movie.common.Constanst.MIN_PAGE
import com.movie.databinding.FragmentTopRatedBinding
import com.movie.model.response.TopRatedResponse
import com.movie.ui.detail.DetailActivity
import com.movie.ui.home.fragment.toprated.adapter.TopRatedAdapter
import java.lang.Boolean.FALSE
import javax.inject.Inject

class TopRatedFragment : BaseFragment(), TopRatedView {

    @Inject
    lateinit var presenter: TopRatedPresenter

    private lateinit var getActivity: FragmentActivity
    private lateinit var binding: FragmentTopRatedBinding
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var topRatedAdapter: TopRatedAdapter = TopRatedAdapter()

    companion object {
        private var currentPage: Int = 1
        private var totalPage: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApp.create(activity).provides().inject(fragment = this)
        activity?.let { getActivity = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTopRatedBinding.inflate(inflater, container, FALSE)
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
            currentPage = MIN_PAGE
            presenter.fetchMovie(page = INIT_PAGE)
        }
    }

    override fun onResume() {
        super.onResume()
        topRatedAdapter.refresh()
    }

    override fun onDetach() {
        super.onDetach()
        presenter.dispose()
        if (!topRatedAdapter.initCheck()) {
            topRatedAdapter.removeLast(presenter.retry())
        } else {
            topRatedAdapter.clear()
        }
    }

    private fun loadData() {
        binding.let {
            when {
                !topRatedAdapter.isAvailable() -> {
                    currentPage = MIN_PAGE
                    presenter.fetchMovie(page = INIT_PAGE)
                }
                currentPage == MIN_PAGE && topRatedAdapter.isAvailable() && totalPage == Constanst.ZERO -> {
                    currentPage = MIN_PAGE
                    presenter.fetchMovie(page = INIT_PAGE)
                }
                topRatedAdapter.isRetry() -> {
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
        topRatedAdapter.getItem { item, _ ->
            if (item is TopRatedResponse.Result) {
                DetailActivity.create().start(getActivity, movieId = item.id.toString())
            } else if (item == Constanst.RETRY) {
                if (currentPage != totalPage) {
                    topRatedAdapter.update(presenter.loadMore())
                    presenter.fetchLoadMore(page = currentPage.toString())
                } else {
                    currentPage = MIN_PAGE
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
                        topRatedAdapter.setLoading(presenter.loadMore())
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
            adapter = topRatedAdapter
        }
    }

    override fun showLoading() {
        if (!binding.swiperefresh.isRefreshing) topRatedAdapter.set(presenter.loadingList())
    }

    override fun hideLoading() {
        binding.swiperefresh.isRefreshing = false
    }

    override fun onError(message: String) {
        binding.recyclerView.removeOnScrollListener(scrollListener)
        topRatedAdapter.set(presenter.retry())
    }

    override fun removeListener() {
        binding.recyclerView.removeOnScrollListener(scrollListener)
    }

    override fun onRetrieveData(model: TopRatedResponse) {
        binding.let {
            totalPage = model.totalPages
            if (totalPage > MIN_PAGE && model.results?.size == MAX_PAGE) {
                it.recyclerView.addOnScrollListener(scrollListener)
            }
            topRatedAdapter.set(model.results?: mutableListOf())
        }
    }

    override fun onRetrieveMoreData(model: TopRatedResponse) {
        binding.let {
            it.recyclerView.removeOnScrollListener(scrollListener)
            it.recyclerView.addOnScrollListener(scrollListener)
            topRatedAdapter.update(model.results?: mutableListOf())
        }
    }
}