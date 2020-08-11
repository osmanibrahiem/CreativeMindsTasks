package com.cems.devtask.model

sealed class ResponseResult<out T> {

    data class Success<out T>(val data: T) : ResponseResult<T>()
    object Loading : ResponseResult<Nothing>()
    data class Error(val message: String?) : ResponseResult<Nothing>()

}