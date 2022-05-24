package com.example.giftwallet.giftlist.db


import android.media.Image
import android.widget.ImageView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

//@Fts4
@Entity(tableName = "BrandEntity", primaryKeys = ["brand"])
data class BrandEntity(
    var brand: String
)
