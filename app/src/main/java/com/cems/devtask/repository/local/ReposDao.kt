package com.cems.devtask.repository.local

import androidx.paging.PagingSource
import androidx.room.*
import com.cems.devtask.model.ReposItem
import io.reactivex.Observable

@Dao
interface ReposDao {

    @Query("SELECT * FROM repos_items")
    fun getAllItems(): PagingSource<Int, ReposItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<ReposItem>)

    @Query("DELETE FROM repos_items")
    suspend fun clearRepos()

}