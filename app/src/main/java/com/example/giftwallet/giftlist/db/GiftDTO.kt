package com.example.giftwallet.giftlist.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GiftDTO(
    @SerializedName("imageurl") val imageurl: String,
    @SerializedName("info") val info: String,
    @SerializedName("brand") var brand: String,
    @SerializedName("validate") val validate: String,
    @SerializedName("useyn") val useyn: Int? = null,
    @SerializedName("orgurl") val orgurl: String
    ): Parcelable