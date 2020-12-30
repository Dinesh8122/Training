package com.example.training.PojoModel

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class NewsDataRes(
//    @Embedded
    @SerializedName("articles") val news: List<News>
)

@Entity(tableName = "newsTable")
data class News(
    val title: String,
    @SerializedName("urlToImage")
    val image: String,
    @PrimaryKey
    @ColumnInfo(name ="publishedAt")
    val publishedAt :String
)

enum class State {
    DONE, LOADING, ERROR
}