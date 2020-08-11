package com.cems.devtask.ui.base

import android.content.Context
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import com.cems.devtask.repository.Repository

open class BaseViewModel(val context: Context, val repository: Repository) : ViewModel() {

    var compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}