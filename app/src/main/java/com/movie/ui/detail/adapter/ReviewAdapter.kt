package com.movie.ui.detail.adapter

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.movie.R
import com.movie.common.BackgroundColor
import com.movie.common.Constanst
import com.movie.common.Constanst.TYPE_CONTENT
import com.movie.common.Constanst.TYPE_RETRY
import com.movie.common.Visibility
import com.movie.databinding.ItemRetryBinding
import com.movie.databinding.ItemReviewBinding
import com.movie.model.response.MovieReviewResponse
import java.lang.Boolean.FALSE
import kotlin.math.roundToInt

class ReviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var models: MutableList<MovieReviewResponse.Result> = mutableListOf()
    private lateinit var listener: (Any, Int) -> Unit

    override fun getItemCount(): Int = models.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CONTENT -> ContentViewHolder(
                DataBindingUtil.inflate(
                    layoutInflater, R.layout.item_review,
                    parent, FALSE
                ) as ItemReviewBinding
            )
            TYPE_RETRY -> RetryViewHolder(
                DataBindingUtil.inflate(
                    layoutInflater, R.layout.item_retry,
                    parent, FALSE
                ) as ItemRetryBinding
            )
            else -> ContentViewHolder(
                DataBindingUtil.inflate(
                    layoutInflater, R.layout.item_review,
                    parent, FALSE
                ) as ItemReviewBinding
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

    fun set(models: MutableList<MovieReviewResponse.Result>) {
        this.models.clear()
        this.models = models
        notifyDataSetChanged()
    }

    fun setLoading(models: MutableList<MovieReviewResponse.Result>) {
        this.models.addAll(models)
        notifyDataSetChanged()
    }

    fun update(list: MutableList<MovieReviewResponse.Result>) {
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

    fun removeLast(list: MutableList<MovieReviewResponse.Result>) {
        if (models[itemCount - 1].isLoading) {
            models.removeAt(index = itemCount - 1)
            this.models.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun isRetry(): Boolean {
        return models[itemCount - 1].type == TYPE_RETRY
    }

    fun initCheck(): Boolean {
        return models.size == 9 && models[itemCount - 1].isLoading
    }

    fun clear() {
        this.models.clear()
        notifyDataSetChanged()
    }

    private inner class ContentViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(listMenu: MutableList<MovieReviewResponse.Result>, position: Int) {
            val model: MovieReviewResponse.Result = listMenu[position]
            showAnimation(model.isLoading)
            binding.let {
                it.textAuthor.text = model.author
                it.textContent.text = model.content
            }
        }

        fun getContext(): Context {
            return binding.root.context
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

    private inner class RetryViewHolder(val binding: ItemRetryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.let {
                it.layoutRetry.layoutParams.width = width().roundToInt()
                it.layoutRetry.layoutParams.height = height().roundToInt()
                it.layoutRetry.requestLayout()
            }
        }

        fun bindItem(position: Int) {
            binding.let {
                it.layoutRetry.setOnClickListener { listener(Constanst.RETRY, position) }
            }
        }

        fun getContext(): Context {
            return binding.root.context
        }

        fun width(): Float {
            val metrics = DisplayMetrics()
            (getContext() as Activity).windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels / 3.0.toFloat()
        }

        fun height(): Float {
            val metrics = DisplayMetrics()
            (getContext() as Activity).windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.heightPixels / 3.7.toFloat()
        }
    }
}