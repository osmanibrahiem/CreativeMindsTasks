package com.cems.devtask.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cems.devtask.model.ReposItem
import com.cems.devtask.model.ResponseResult
import com.cems.devtask.repository.Repository
import com.cems.devtask.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow

class MainViewModel(context: Context, repository: Repository) : BaseViewModel(context, repository) {

    private var currentSearchResult: Flow<PagingData<ReposItem>>? = null

    fun fetchRepositories(): Flow<PagingData<ReposItem>> {
        val lastResult = currentSearchResult
        if (lastResult != null) {
            return lastResult
        }
        val newResult: Flow<PagingData<ReposItem>> = repository.fetchRepositories()
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }
}