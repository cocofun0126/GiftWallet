package com.example.giftwallet.giftlist.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GiftDao {
    @Query("SELECT * FROM GIFTENTITY")
    fun getAll() : List<GiftEntity>

    @Insert
    fun insertGift(gift:GiftEntity)
    @Delete
    fun deleteGift(gift:GiftEntity)
}