package com.cems.devtask.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cems.devtask.helper.extensions.isNetworkConnected
import com.cems.devtask.model.ReposItem
import com.cems.devtask.model.ResponseResult
import com.cems.devtask.repository.Repository
import com.cems.devtask.ui.base.BaseViewModel

class MainViewModel(context: Context, repository: Repository) : BaseViewModel(context, repository) {

    fun fetchRepositories(page: Int?): LiveData<ResponseResult<List<ReposItem>>> {
        val result = MutableLiveData<ResponseResult<List<ReposItem>>>()
        result.value = ResponseResult.Loading
        repository.fetchRepositories(
            compositeDisposable,
            context.isNetworkConnected(),
            page,
            result::setValue
        )
        return result
    }
}