package com.example.training.RoomDataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.training.PojoModel.News

@Dao
interface NewsListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(news: News)


    @Query("select * from newsTable")
    fun getNewsList(): LiveData<News>

    @Query("DELETE FROM newsTable WHERE publishedAt NOT IN(:publishedAt)")
    fun removeOldNews(publishedAt:List<String>)

    @Query("select * from newsTable where publishedAt=(:publishedAt)")
    fun selectNews(publishedAt:String) : News

    @Query("select * from newsTable")
    fun getNews(): News

}