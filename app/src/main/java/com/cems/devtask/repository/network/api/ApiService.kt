package com.cems.devtask.repository.network.api

import com.cems.devtask.BuildConfig
import com.cems.devtask.helper.Constants
import com.cems.devtask.model.ReposItem
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users/{username}/repos")
    suspend fun fetchRepositories(
        @Path("username") username: String? = Constants.REPOSITORIES_USERNAME,
        @Query("access_token") token: String? = BuildConfig.ACCESS_TOKEN,
        @Query("per_page") pageSize: Int? = Constants.PAGE_SIZE,
        @Query("page") currentPage: Int?
    ): List<ReposItem>

}