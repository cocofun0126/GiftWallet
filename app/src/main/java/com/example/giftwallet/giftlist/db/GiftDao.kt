package com.example.giftwallet.giftlist.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GiftDao {

    @Transaction
    @Query("SELECT * FROM GIFTENTITY")
    fun getAllGift() : List<GiftEntity>

//  contenturl로 조회해서 값이 있는지 확인 후 저장 진행
    @Transaction
    @Query("SELECT COUNT(*) FROM GIFTENTITY WHERE upper(trim(orgurl)) like upper(trim(:url))")
    fun getUrlCount(url:String) : Int

    @Transaction
    @Insert
    fun insertGift(gift:GiftEntity)

    @Transaction
    @Insert
    fun updateGift(gift:GiftEntity)

    @Transaction
    @Delete
    fun deleteGift(gift:GiftEntity)

//    https://androidkt.com/datetime-datatype-sqlite-using-room/
//    https://www.w3resource.com/sqlite/sqlite-strftime.php
    @Query("SELECT imageurl, info, brand,  validate, useyn, orgurl  FROM GIFTENTITY WHERE USEYN = 1 AND cast(strftime('%Y%m%d', 'now') -  VALIDATE as int) < 100 ORDER BY VALIDATE ASC ")
    fun date_calc():List<GiftEntity>?

}
