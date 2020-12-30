package com.example.training.pagination

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.training.ApiService
import com.example.training.PojoModel.News
import io.reactivex.disposables.CompositeDisposable

class NewsDataSourceFactory(
    private val compositeDisposable: CompositeDisposable,
    private val networkService: ApiService
)
    : DataSource.Factory<Int, News>() {

    val newsDataSourceLiveData = MutableLiveData<NewsDataSource>()

    override fun create(): DataSource<Int, News> {
        val newsDataSource = NewsDataSource(networkService, compositeDisposable)
        newsDataSourceLiveData.postValue(newsDataSource)
        return newsDataSource
    }
}