package com.cems.devtask.repository.network.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.cems.devtask.helper.Constants
import com.cems.devtask.model.RemoteKeys
import com.cems.devtask.model.ReposItem
import com.cems.devtask.repository.local.ReposDatabase
import com.cems.devtask.repository.network.api.ApiService
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import okio.IOException
import java.io.InvalidObjectException

@OptIn(ExperimentalPagingApi::class)
class ReposRemoteMediator(
    private val service: ApiService,
    private val database: ReposDatabase
) : RemoteMediator<Int, ReposItem>() {

    private val reposDao = database.reposDao()
    private val remoteKeyDao = database.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ReposItem>
    ): MediatorResult {
        try {
            // Get the closest item from PagingState that we want to load data around.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.plus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    if (remoteKeys == null) {
                        // The LoadType is PREPEND so some data was loaded before,
                        // so we should have been able to get remote keys
                        // If the remoteKeys are null, then we're an invalid state and we have a bug
                        throw InvalidObjectException("Remote key and the prevKey should not be null")
                    }
                    // If the previous key is null, then we can't request more data
                    val prevKey = remoteKeys.prevKey
                    if (prevKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKeys.prevKey
                }
                LoadType.APPEND -> {
                    // Query DB for SubredditRemoteKey for the subreddit.
                    // SubredditRemoteKey is a wrapper object we use to keep track of page keys we
                    // receive from the Reddit API to fetch the next or previous page.
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    if (remoteKeys == null || remoteKeys.nextKey == null) {
                        throw InvalidObjectException("Remote key should not be null for $loadType")
                    }
                    remoteKeys.nextKey

                }
            }

            val data = service.fetchRepositories(
                currentPage = loadKey
            )

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    reposDao.clearRepos()
                    remoteKeyDao.clearRemoteKeys()
                }
                val keys = data.map {
                    RemoteKeys(
                        repoId = it.id,
                        prevKey = null,
                        nextKey = if (data.isEmpty()) null else loadKey + 1
                    )
                }
                remoteKeyDao.insertAll(keys)
                reposDao.insertAll(data)
            }

            return MediatorResult.Success(endOfPaginationReached = data.isEmpty())
        } catch (e: java.io.IOException) {
            return MediatorResult.Error(e)
        } catch (e: retrofit2.HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ReposItem>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                // Get the remote keys of the last item retrieved
                database.remoteKeysDao().remoteKeysRepoId(repo.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ReposItem>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // Get the remote keys of the first items retrieved
                database.remoteKeysDao().remoteKeysRepoId(repo.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ReposItem>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                database.remoteKeysDao().remoteKeysRepoId(repoId)
            }
        }
    }
}