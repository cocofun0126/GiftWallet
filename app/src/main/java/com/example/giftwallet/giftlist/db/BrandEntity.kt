package com.example.giftwallet.giftlist.db


import android.media.Image
import android.widget.ImageView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BrandEntity")
data class BrandEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "brand") var brand: String
)