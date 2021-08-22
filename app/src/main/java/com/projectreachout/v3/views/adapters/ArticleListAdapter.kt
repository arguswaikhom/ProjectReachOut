package com.projectreachout.v3.views.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.projectreachout.v3.models.ArticlePost
import com.projectreachout.v3.views.viewholders.ArticleVH

class ArticleListAdapter<T> :
    ListAdapter<T, ArticleVH>(ArticleComparator()) {

    class ArticleComparator<T> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return (oldItem as ArticlePost).myReaction == (newItem as ArticlePost).myReaction
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH =
        ArticleVH.create(parent)

    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        holder.bind(getItem(position) as ArticlePost)
    }
}