package com.cems.devtask.repository

import com.cems.devtask.helper.Constants
import com.cems.devtask.model.ReposItem
import com.cems.devtask.model.ResponseResult
import com.cems.devtask.repository.local.ReposDao
import com.cems.devtask.repository.network.api.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Repository(
    val service: ApiService,
    val database: ReposDao
) {

    fun fetchRepositories(
        compositeDisposable: CompositeDisposable,
        isOnline: Boolean,
        page: Int?,
        callback: (ResponseResult<List<ReposItem>>) -> Unit
    ) {
        if (!isOnline && ((page == null || page <= 1)))
            fetchLocalRepositories(compositeDisposable, callback)
        else
            fetchApiRepositories(compositeDisposable, page, callback)
    }

    private fun fetchApiRepositories(
        compositeDisposable: CompositeDisposable,
        page: Int?,
        callback: (ResponseResult<List<ReposItem>>) -> Unit
    ) {
        compositeDisposable.add(
            service.fetchRepositories(
                username = Constants.REPOSITORIES_USERNAME,
                token = null,
                pageSize = Constants.PAGE_SIZE,
                currentPage = page
            )
                .subscribeOn(Schedulers.io())
                .doOnNext { list ->
                    if (page == null || page <= 1) {
                        database.clearRepos()
                    }
                    database.insertAll(list)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response -> callback.invoke(ResponseResult.Success(response)) },
                    { throwable -> callback.invoke(ResponseResult.Error(throwable)) }
                )
        )
    }

    private fun fetchLocalRepositories(
        compositeDisposable: CompositeDisposable,
        callback: (ResponseResult<List<ReposItem>>) -> Unit
    ) {
        compositeDisposable.add(
            database.getAllItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response -> callback.invoke(ResponseResult.Success(response)) },
                    { throwable -> callback.invoke(ResponseResult.Error(throwable)) }
                )
        )
    }

    fun refreshRepositories(
        compositeDisposable: CompositeDisposable,
        callback: (ResponseResult<List<ReposItem>>) -> Unit
    ) = fetchApiRepositories(compositeDisposable, null, callback)

}