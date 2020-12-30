package com.example.training.RoomDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.training.PojoModel.News


@Database(entities = [News::class], version = 8, exportSchema = false)
abstract class NewsDb: RoomDatabase() {
    abstract fun NewsListDao(): NewsListDao
    companion object {
        private var INSTANCE: NewsDb? = null

        fun getDatabseInstance(context: Context): NewsDb {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, NewsDb::class.java, "NewsDb.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE as NewsDb
        }
    }

}