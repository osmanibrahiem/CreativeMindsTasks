package com.cems.devtask.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cems.devtask.model.ReposItem
import com.cems.devtask.ui.adapter.viewHolder.ReposViewHolder

class ReposAdapter(private val listener: ((ReposItem) -> Unit)? = null) :
    PagingDataAdapter<ReposItem, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReposViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            (holder as ReposViewHolder).bind(repoItem, listener)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<ReposItem>() {
            override fun areItemsTheSame(oldItem: ReposItem, newItem: ReposItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ReposItem, newItem: ReposItem) =
                oldItem == newItem
        }
    }
}
