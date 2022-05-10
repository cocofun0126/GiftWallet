package com.example.giftwallet.giftlist.db

import android.media.Image
import android.widget.ImageView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GiftEntity")
data class GiftEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "importance") var importance: Int,
    @ColumnInfo(name = "imagerurl") var imagerurl: String
    )