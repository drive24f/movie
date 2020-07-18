package com.movie.ui.home.fragment.popular.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.movie.R
import com.movie.common.*
import com.movie.common.Constanst.TYPE_CONTENT
import com.movie.common.Constanst.TYPE_RETRY
import com.movie.databinding.ItemPopularBinding
import com.movie.databinding.ItemRetryBinding
import com.movie.db.FavManager
import com.movie.model.response.PopularResponse
import java.lang.Boolean.FALSE

class PopularAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var models: MutableList<PopularResponse.Result> = mutableListOf()
    private lateinit var listener: (Any, Int) -> Unit

    override fun getItemCount(): Int = models.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CONTENT -> ContentViewHolder(
                DataBindingUtil.inflate(
                    layoutInflater, R.layout.item_popular,
                    parent, FALSE
                ) as ItemPopularBinding
            )
            Constanst.TYPE_RETRY -> RetryViewHolder(
                DataBindingUtil.inflate(
                    layoutInflater, R.layout.item_retry,
                    parent, FALSE
                ) as ItemRetryBinding
            )
            else -> ContentViewHolder(
                DataBindingUtil.inflate(
                    layoutInflater, R.layout.item_popular,
                    parent, FALSE
                ) as ItemPopularBinding
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, p1: Int) {
        when (holder) {
            is ContentViewHolder -> holder.bindItem(models, p1)
            is RetryViewHolder -> holder.bindItem(p1)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (models[position].type) {
            TYPE_CONTENT -> TYPE_CONTENT
            TYPE_RETRY -> TYPE_RETRY
            else -> TYPE_CONTENT
        }
    }

    fun set(models: MutableList<PopularResponse.Result>) {
        this.models.clear()
        this.models = models
        notifyDataSetChanged()
    }

    fun setLoading(models: MutableList<PopularResponse.Result>) {
        this.models.addAll(models)
        notifyDataSetChanged()
    }

    fun update(list: MutableList<PopularResponse.Result>) {
        this.models.removeAt(index = itemCount - 1)
        this.models.addAll(list)
        notifyDataSetChanged()
    }

    fun refresh() {
        notifyDataSetChanged()
    }

    fun getItem(listener: (Any, Int) -> Unit) {
        this.listener = listener
    }

    fun isAvailable(): Boolean {
        return models.size != 0
    }

    fun removeLast(list: MutableList<PopularResponse.Result>) {
        if (models[itemCount - 1].isLoading) {
            models.removeAt(index = itemCount - 1)
            this.models.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun isRetry(): Boolean {
        return models[itemCount - 1].type == Constanst.TYPE_RETRY
    }

    fun initCheck(): Boolean {
        return models.size == 3 && models[itemCount - 1].isLoading
    }

    fun clear() {
        this.models.clear()
        notifyDataSetChanged()
    }

    private inner class ContentViewHolder(val binding: ItemPopularBinding) : RecyclerView.ViewHolder(binding.root) {

        private var favManager: FavManager = FavManager(getContext())

        fun bindItem(listMenu: MutableList<PopularResponse.Result>, position: Int) {
            val model: PopularResponse.Result = listMenu[position]
            showAnimation(model.isLoading)
            showStar(model)
            binding.let {
                if (!model.isLoading) {
                    it.textTitle.text = model.title
                    it.textDate.text = model.releaseDate.formatDate()
                    it.textDesc.text = model.overview
                    it.imageMovie.setImage(Constanst.IMAGE_URL + model.posterPath)
                    it.root.setOnClickListener { listener(model, position) }
                }
            }
        }

        fun getContext(): Context {
            return binding.root.context
        }

        fun showStar(model: PopularResponse.Result) {
            binding.let {
                when (favManager.isAvailable(model.id)) {
                    true -> it.imageBookmark.visibility = VISIBLE
                    false -> it.imageBookmark.visibility = GONE
                }
            }
        }

        fun showAnimation(isAnimate: Boolean) {
            when {
                isAnimate -> loadAnimation()
                else -> unLoadAnimation()
            }
        }

        private fun loadAnimation() {
            val bgColor: Int = ContextCompat.getColor(getContext(), R.color.unselect_grey)
            binding.let {
                it.colorItem = BackgroundColor(bgColor)
                it.visibility = Visibility(isVisible = false)
                it.slRoot.startShimmerAnimation()
            }
        }

        private fun unLoadAnimation() {
            val bgColor: Int = ContextCompat.getColor(getContext(), android.R.color.transparent)
            binding.let {
                it.colorItem = BackgroundColor(bgColor)
                it.visibility = Visibility(isVisible = true)
                it.slRoot.stopShimmerAnimation()
            }
        }
    }

    private inner class RetryViewHolder(val binding: ItemRetryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(position: Int) {
            binding.let {
                it.layoutRetry.setOnClickListener { listener(Constanst.RETRY, position) }
            }
        }
    }
}