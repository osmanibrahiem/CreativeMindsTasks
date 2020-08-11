package com.cems.devtask.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cems.devtask.model.Owner
import com.cems.devtask.model.RemoteKeys
import com.cems.devtask.model.ReposItem

@Database(
    entities = [ReposItem::class, Owner::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class ReposDatabase : RoomDatabase() {

    abstract fun reposDao(): ReposDao
    abstract fun remoteKeysDao(): RemoteKeysDao

}