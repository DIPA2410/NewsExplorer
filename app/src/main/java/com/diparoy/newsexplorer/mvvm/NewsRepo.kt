package com.diparoy.newsexplorer.mvvm

import com.diparoy.newsexplorer.service.RetrofitInstance

class NewsRepo() {
    suspend fun getBreakingNews(code: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(code, pageNumber)

    suspend fun getCategoryNews(code: String) = RetrofitInstance.api.getByCategory(code)
}