package com.cems.devtask.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cems.devtask.model.ReposItem
import io.reactivex.Observable

@Dao
interface ReposDao {

    @Query("SELECT * FROM repos_items")
    fun getAllItems(): Observable<List<ReposItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(repos: List<ReposItem>)

    @Query("DELETE FROM repos_items")
    fun clearRepos()

}