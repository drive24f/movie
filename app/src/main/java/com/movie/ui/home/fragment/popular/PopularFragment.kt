package com.movie.ui.home.fragment.popular

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
import com.movie.databinding.FragmentPopularBinding
import com.movie.model.response.PopularResponse
import com.movie.ui.detail.DetailActivity
import com.movie.ui.home.fragment.popular.adapter.PopularAdapter
import java.lang.Boolean.FALSE
import javax.inject.Inject

class PopularFragment : BaseFragment(), PopularView {

    @Inject
    lateinit var presenter: PopularPresenter

    private lateinit var getActivity: FragmentActivity
    private lateinit var binding: FragmentPopularBinding
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var popularAdapter: PopularAdapter = PopularAdapter()

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
        binding = FragmentPopularBinding.inflate(inflater, container, FALSE)
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
            presenter.fetchMovie(page = Constanst.INIT_PAGE)
        }
    }

    override fun onResume() {
        super.onResume()
        popularAdapter.refresh()
    }

    override fun onDetach() {
        super.onDetach()
        presenter.dispose()
        if (!popularAdapter.initCheck()) {
            popularAdapter.removeLast(presenter.retry())
        } else {
            popularAdapter.clear()
        }
    }

    private fun loadData() {
        binding.let {
            when {
                !popularAdapter.isAvailable() -> {
                    currentPage = Constanst.MIN_PAGE
                    presenter.fetchMovie(page = Constanst.INIT_PAGE)
                }
                currentPage == Constanst.MIN_PAGE && popularAdapter.isAvailable() && totalPage == Constanst.ZERO -> {
                    currentPage = Constanst.MIN_PAGE
                    presenter.fetchMovie(page = Constanst.INIT_PAGE)
                }
                popularAdapter.isRetry() -> {
                    it.recyclerView.removeOnScrollListener(scrollListener)
                }
                else -> {
                    if (totalPage > Constanst.MIN_PAGE) {
                        it.recyclerView.addOnScrollListener(scrollListener)
                    }
                }
            }
        }
    }

    private fun initButton() {
        popularAdapter.getItem { item, _ ->
            if (item is PopularResponse.Result) {
                DetailActivity.create().start(getActivity, movieId = item.id.toString())
            } else if (item == Constanst.RETRY) {
                if (currentPage != totalPage) {
                    popularAdapter.update(presenter.loadMore())
                    presenter.fetchLoadMore(page = currentPage.toString())
                }
            }
        }
    }

    private fun initScroll() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount: Int = recyclerView.layoutManager!!.itemCount
                if (totalItemCount == linearLayoutManager.findLastVisibleItemPosition() + Constanst.MIN_PAGE) {
                    currentPage++
                    binding.recyclerView.removeOnScrollListener(scrollListener)
                    if (currentPage != totalPage) {
                        popularAdapter.setLoading(presenter.loadMore())
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
            adapter = popularAdapter
        }
    }

    override fun showLoading() {
        if (!binding.swiperefresh.isRefreshing) popularAdapter.set(presenter.loadingList())
    }

    override fun hideLoading() {
        // do something here
    }

    override fun onError(message: String) {
        binding.recyclerView.removeOnScrollListener(scrollListener)
        popularAdapter.update(presenter.retry())
    }

    override fun removeListener() {
        binding.recyclerView.removeOnScrollListener(scrollListener)
    }

    override fun onRetrieveData(model: PopularResponse) {
        totalPage = model.totalPages
        if (totalPage > Constanst.MIN_PAGE && model.results?.size == Constanst.MAX_PAGE) {
            binding.recyclerView.addOnScrollListener(scrollListener)
        }
        binding.swiperefresh.isRefreshing = false
        popularAdapter.set(model.results ?: mutableListOf())
    }

    override fun onRetrieveMoreData(model: PopularResponse) {
        binding.let {
            binding.recyclerView.removeOnScrollListener(scrollListener)
            binding.recyclerView.addOnScrollListener(scrollListener)
            popularAdapter.update(model.results ?: mutableListOf())
        }
    }
}