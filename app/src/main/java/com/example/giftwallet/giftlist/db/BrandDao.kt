package com.example.giftwallet.giftlist.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BrandDao {
    @Query("SELECT * FROM BRANDENTITY")
    fun getAllBrand() : List<BrandEntity>

    @Insert
    fun insertBrand(brand:BrandEntity)
    @Delete
    fun deleteBrand(brand:BrandEntity)
}