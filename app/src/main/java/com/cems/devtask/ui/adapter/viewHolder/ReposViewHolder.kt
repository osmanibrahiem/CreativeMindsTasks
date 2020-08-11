package com.cems.devtask.ui.adapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cems.devtask.databinding.RepoViewItemBinding
import com.cems.devtask.model.ReposItem

class ReposViewHolder(private val binding: RepoViewItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(reposItem: ReposItem, listener: ((ReposItem) -> Unit)? = null) {
        binding.reposItem = reposItem
        binding.executePendingBindings()

        binding.cardView.setOnClickListener { }
        binding.cardView.setOnLongClickListener {
            listener?.invoke(reposItem)
            true
        }
    }

    companion object {
        fun create(parent: ViewGroup): ReposViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RepoViewItemBinding.inflate(inflater, parent, false)
            return ReposViewHolder(binding)
        }
    }
}