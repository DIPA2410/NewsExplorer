package com.diparoy.newsexplorer.service

import com.diparoy.newsexplorer.Utils.Companion.API_KEY
import com.diparoy.newsexplorer.db.News
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countrycode: String = "in",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey : String = API_KEY
    ): Response<News>

    @GET("v2/everything")
    suspend fun getByCategory(
        @Query("q")
        category: String = "",
        @Query("apiKey")
        apiKey : String = API_KEY
    ): Response<News>
}