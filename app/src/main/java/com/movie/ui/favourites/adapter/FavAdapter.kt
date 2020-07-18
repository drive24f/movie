package com.movie.ui.favourites.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.movie.R
import com.movie.common.Constanst
import com.movie.common.formatDate
import com.movie.common.setImage
import com.movie.databinding.ItemFavouriteBinding
import com.movie.db.FavManager
import com.movie.db.FavUser
import java.lang.Boolean.FALSE

class FavAdapter : RecyclerView.Adapter<FavAdapter.BindingHolder>() {

    private var models: MutableList<FavUser> = mutableListOf()

    private lateinit var listener: (Any, Int) -> Unit

    override fun getItemCount(): Int = models.size

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): BindingHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite, parent, FALSE)
        return BindingHolder(v)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        holder.bind(models[position], listener, position)
    }

    fun set(models: MutableList<FavUser>) {
        this.models.clear()
        this.models = models
        notifyDataSetChanged()
    }

    fun getItem(listener: (Any, Int) -> Unit) {
        this.listener = listener
    }

    class BindingHolder(private val rowView: View) : RecyclerView.ViewHolder(rowView) {

        private var binding: ItemFavouriteBinding? = DataBindingUtil.bind(rowView)
        private var favManager: FavManager = FavManager(getContext())

        fun bind(model: FavUser, listener: (Any, Int) -> Unit, position: Int) {
            binding?.let {
                it.textTitle.text = model.title
                it.textDate.text = model.releaseDate.formatDate()
                it.textDesc.text = model.overview
                it.imageMovie.setImage(Constanst.IMAGE_URL + model.posterPath)
                it.root.setOnClickListener { listener(model, position) }

                when (favManager.isAvailable(model.idMovie)) {
                    true -> it.imageBookmark.visibility = VISIBLE
                    false -> it.imageBookmark.visibility = GONE
                }
            }
        }

        private fun getContext(): Context {
            return rowView.context
        }
    }
}