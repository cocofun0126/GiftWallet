package com.example.giftwallet.giftlist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "GiftEntity")
data class GiftEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "imageurl") var imageurl: String,
    @ColumnInfo(name = "info") var info: String,
    @ColumnInfo(name = "brand") var brand: String,
    @ColumnInfo(name = "validate") var validate: String?,
    @ColumnInfo(name = "useyn") var useyn: Int? = null,
    @ColumnInfo(name = "orgurl") var orgurl: String
    ): Serializable