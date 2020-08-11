package com.cems.devtask.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "owners")
data class Owner(
    @PrimaryKey
    @ColumnInfo(name = "owner_id")
    @SerializedName("id")
    var id: Int = 0,
    @ColumnInfo(name = "owner_login")
    @SerializedName("login")
    var login: String? = null,
    @ColumnInfo(name = "owner_avatar_url")
    @SerializedName("avatar_url")
    var avatarUrl: String? = null,
    @ColumnInfo(name = "owner_html_url")
    @SerializedName("html_url")
    var htmlUrl: String? = null
)