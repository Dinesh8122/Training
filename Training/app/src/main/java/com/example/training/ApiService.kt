package com.example.training

import com.example.training.PojoModel.NewsDataRes
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
//
//    @POST("v2/checkphone")
//    fun checkUser(@Body user: CheckUserReq): Observable<CheckUser>

    @GET("everything?q=bitcoin&from=2020-11-29&sortBy=publishedAt&apiKey=22b4fbcf3a2d491d87d7435e1ea83061")
    fun getNews(@Query("page") page: Int, @Query("pageSize") pageSize: Int): Observable<NewsDataRes>

    companion object Factory {

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://newsapi.org/v2/")
                .build()

            return retrofit.create(ApiService::class.java);
        }
    }
}