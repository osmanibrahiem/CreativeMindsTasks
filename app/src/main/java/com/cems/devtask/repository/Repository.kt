package com.cems.devtask.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cems.devtask.helper.Constants
import com.cems.devtask.model.ReposItem
import com.cems.devtask.repository.local.ReposDatabase
import com.cems.devtask.repository.network.paging.ReposRemoteMediator
import com.cems.devtask.repository.network.api.ApiService
import kotlinx.coroutines.flow.Flow

class Repository(
    val service: ApiService,
    val database: ReposDatabase
) {

    fun fetchRepositories(): Flow<PagingData<ReposItem>> {
        return Pager(
            config = PagingConfig(pageSize = Constants.PAGE_SIZE),
            remoteMediator = ReposRemoteMediator(
                service,
                database
            ),
            pagingSourceFactory = { database.reposDao().getAllItems() }
        ).flow
    }
}