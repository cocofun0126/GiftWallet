package com.example.giftwallet.giftlist.db

import android.media.Image
import android.widget.ImageView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

//@Fts4
@Entity(tableName = "GiftEntity")
data class GiftEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "imagerurl") var imagerurl: String,
    @ColumnInfo(name = "info") var info: String,
    @ColumnInfo(name = "brand") var brand: String,
    @ColumnInfo(name = "validate") var validate: String,
    )