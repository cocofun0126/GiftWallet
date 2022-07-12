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



//    @Query("SELECT * FROM GIFTENTITY WHERE USEYN = 1 AND  JULIANDAY(VALIDATE) - JULIANDAY('now') ")
//    @Query("SELECT (JULIANDAY(VALIDATE) - JULIANDAY('now')) as 'date_diff', orgurl  FROM GIFTENTITY WHERE USEYN = 1 ")
    @Query("SELECT imageurl, info, brand, cast(strftime('%YYYY%mm%dd', 'now') - strftime('%YYYY%mm%dd', VALIDATE) as int) as validate, useyn, orgurl  FROM GIFTENTITY WHERE USEYN = 1 ")
    fun date_calc():GiftEntity?

}
