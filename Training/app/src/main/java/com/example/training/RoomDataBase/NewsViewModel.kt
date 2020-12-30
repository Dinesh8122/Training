package com.example.training.RoomDataBase

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.training.PojoModel.News

class NewsViewModel(application: Application): AndroidViewModel(application) {


    private var userList: LiveData<News>
    private var appDb: NewsDb

    init {
        appDb= NewsDb.getDatabseInstance(this.getApplication())
        userList=appDb.NewsListDao().getNewsList()
    }

    fun getNews():LiveData<News>{
        return  userList
    }

    fun addNews(news:News){
        addUserAsync(appDb).execute(news)
    }

    class addUserAsync(db: NewsDb): AsyncTask<News, Void, Void>(){
        private var userDb: NewsDb =db
        override fun doInBackground(vararg params: News): Void? {
            Log.d("userListViewModel","inserting user")
            userDb.NewsListDao().insertUser(params[0])
            return null
        }
    }

}
