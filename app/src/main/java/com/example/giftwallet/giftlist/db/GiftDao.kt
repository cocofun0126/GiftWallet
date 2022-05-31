package com.example.giftwallet.giftlist.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GiftDao {
    @Query("SELECT * FROM GIFTENTITY")
    fun getAllGift() : List<GiftEntity>

//  contenturl로 조회해서 값이 있는지 확인 후 저장 진행
    @Query("SELECT COUNT(*) FROM GIFTENTITY WHERE imagerurl = :url")
    fun getGiftCheckUrl(url:String) : Int

    @Insert
    fun insertGift(gift:GiftEntity)
    @Delete
    fun deleteGift(gift:GiftEntity)


}
