package com.example.studita.data.cache.interesting

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class InterestingCacheImpl(private val sharedPreferences: SharedPreferences) : InterestingCache {

    companion object {
        const val INTERESTING_PREFS = "interesting_cache"
    }

    override fun saveInterestingListJson(json: String) {
        val interestingList =
            Gson().fromJson<List<JsonObject>>(json, object : TypeToken<List<JsonObject>>() {}.type)
        interestingList.forEachIndexed { index, interesting ->
            sharedPreferences.edit()
                .putString("${INTERESTING_PREFS}_${index + 1}", interesting.toString()).apply()
        }
    }

    override fun getInterestingJson(interestingNumber: Int): String? =
        sharedPreferences.getString("${INTERESTING_PREFS}_$interestingNumber", null)

    override fun isCached(interestingNumber: Int): Boolean {
        val value = sharedPreferences.getString("${INTERESTING_PREFS}_$interestingNumber", null)
        return value?.isNotEmpty() ?: false
    }

}