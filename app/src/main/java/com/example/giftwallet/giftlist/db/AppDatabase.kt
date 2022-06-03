package com.example.giftwallet.giftlist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

@Database(entities = arrayOf(GiftEntity::class, BrandEntity::class), version = 36)

abstract class AppDatabase :RoomDatabase() {
    abstract fun getGiftDao():GiftDao
    abstract fun getBrandDao():BrandDao


    companion object{
        val databaseName = "db_gift"
        var appDatabase : AppDatabase? = null

        fun getInstance(context:Context) : AppDatabase?{
            if(appDatabase == null){
                appDatabase = Room.databaseBuilder(context,AppDatabase::class.java,databaseName)
                    .createFromAsset("database/BrandEntity.db")
//                    .createFromFile(File("database/BrandEntity.db"))
                    .fallbackToDestructiveMigration() //앞 DB 삭제 후 다시 실행
                    .build()
            }
            return appDatabase
        }
    }
}
