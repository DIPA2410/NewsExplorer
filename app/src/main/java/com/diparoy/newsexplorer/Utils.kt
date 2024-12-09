package com.diparoy.newsexplorer

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class Utils {
    companion object {
        const val API_KEY = "bf0deac63c3548f3b5108ece1dc6889f"
        const val BASE_URL = "https://newsapi.org"

        fun DateFormat(oldstringDate: String?): String? {
            val newDate: String?
            val dateFormat = SimpleDateFormat("dd MM yyyy", Locale(getCountry()))
            newDate = try {
                val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldstringDate)
                dateFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
                oldstringDate
            }
            return newDate
        }

        fun getCountry(): String {
            val locale = Locale.getDefault()
            val country = java.lang.String.valueOf(locale.country)
            return country.lowercase(Locale.getDefault())
        }
    }
}