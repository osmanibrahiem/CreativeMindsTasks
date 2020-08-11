package com.cems.devtask.ui.adapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.cems.devtask.databinding.ReposLoadStateItemBinding

class LoadStateViewHolder(
    private val binding: ReposLoadStateItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        loadState: LoadState,
        retry: () -> Unit
    ) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState !is LoadState.Loading
        binding.errorMsg.isVisible = loadState !is LoadState.Loading

        binding.retryButton.setOnClickListener { retry.invoke() }

    }

    companion object {
        fun create(parent: ViewGroup): LoadStateViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ReposLoadStateItemBinding.inflate(inflater, parent, false)
            return LoadStateViewHolder(binding)
        }
    }

}