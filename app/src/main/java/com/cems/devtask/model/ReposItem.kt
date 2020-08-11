package com.cems.devtask.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "repos_items")
data class ReposItem(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    @SerializedName("id")
    var id: Long = 0,
    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name: String? = null,
    @ColumnInfo(name = "full_name")
    @SerializedName("full_name")
    var fullName: String? = null,
    @Embedded
    @SerializedName("owner")
    var owner: Owner? = null,
    @ColumnInfo(name = "html_url")
    @SerializedName("html_url")
    var htmlUrl: String? = null,
    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description: String? = null,
    @ColumnInfo(name = "fork")
    @SerializedName("fork")
    var isFork: Boolean = false,
    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    var createdAt: String? = null,
    @ColumnInfo(name = "stargazers_count")
    @SerializedName("stargazers_count")
    var stargazersCount: Int = 0,
    @ColumnInfo(name = "language")
    @SerializedName("language")
    var language: String? = null,
    @ColumnInfo(name = "forks_count")
    @SerializedName("forks_count")
    var forksCount: Int = 0
)